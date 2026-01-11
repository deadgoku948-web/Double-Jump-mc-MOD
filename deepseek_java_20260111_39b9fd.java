package com.example.doublejump;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentText;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.input.Keyboard;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class DoubleJumpHandler {
    
    // Stamina system
    private float stamina = 100.0F;
    private final float MAX_STAMINA = 100.0F;
    private final float STAMINA_REGEN_RATE = 0.5F;
    private final float JUMP_STAMINA_COST = 25.0F;
    private boolean hasDoubleJumped = false;
    private int jumpCooldown = 0;
    private final int JUMP_COOLDOWN_TICKS = 5;
    
    // Key binding for manual stamina recharge
    private KeyBinding rechargeKey = new KeyBinding("Recharge Stamina", Keyboard.KEY_R, "Double Jump Mod");
    
    @SubscribeEvent
    public void onPlayerJump(LivingEvent.LivingJumpEvent event) {
        if (event.entity instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) event.entity;
            
            if (player.onGround) {
                // First jump - reset double jump flag
                hasDoubleJumped = false;
            }
        }
    }
    
    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void onKeyInput(InputEvent.KeyInputEvent event) {
        Minecraft mc = Minecraft.getMinecraft();
        EntityPlayerSP player = mc.thePlayer;
        
        if (player == null) return;
        
        // Check for spacebar press in air for double jump
        if (Keyboard.isKeyDown(Keyboard.KEY_SPACE) && !player.onGround && !player.isInWater() && !player.isRiding()) {
            if (!hasDoubleJumped && jumpCooldown <= 0 && stamina >= JUMP_STAMINA_COST) {
                // Perform double jump
                player.motionY = 0.6; // Slightly less than normal jump
                player.fallDistance = 0;
                hasDoubleJumped = true;
                stamina -= JUMP_STAMINA_COST;
                jumpCooldown = JUMP_COOLDOWN_TICKS;
                
                // Play jump sound
                player.playSound("game.player.hurt.fall.big", 0.5F, 1.2F);
            }
        }
        
        // Manual stamina recharge
        if (rechargeKey.isPressed()) {
            if (stamina < MAX_STAMINA && player.onGround) {
                stamina = Math.min(MAX_STAMINA, stamina + 50.0F);
                player.addChatMessage(new ChatComponentText("§aStamina recharged!"));
            }
        }
    }
    
    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.side == Side.CLIENT && event.phase == TickEvent.Phase.END) {
            EntityPlayer player = event.player;
            
            // Regenerate stamina when on ground
            if (player.onGround) {
                stamina = Math.min(MAX_STAMINA, stamina + STAMINA_REGEN_RATE);
            }
            
            // Update cooldown
            if (jumpCooldown > 0) {
                jumpCooldown--;
            }
            
            // Reset double jump flag when on ground
            if (player.onGround) {
                hasDoubleJumped = false;
            }
        }
    }
    
    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void onRenderOverlay(RenderGameOverlayEvent.Post event) {
        if (event.type != RenderGameOverlayEvent.ElementType.TEXT) return;
        
        Minecraft mc = Minecraft.getMinecraft();
        EntityPlayerSP player = mc.thePlayer;
        if (player == null) return;
        
        // Draw stamina bar
        int screenWidth = event.resolution.getScaledWidth();
        int screenHeight = event.resolution.getScaledHeight();
        
        int barWidth = 100;
        int barHeight = 8;
        int x = screenWidth / 2 - barWidth / 2;
        int y = screenHeight - 30;
        
        // Background
        drawRect(x - 1, y - 1, x + barWidth + 1, y + barHeight + 1, 0xFF000000);
        
        // Stamina fill
        int fillWidth = (int)((stamina / MAX_STAMINA) * barWidth);
        int color;
        if (stamina > JUMP_STAMINA_COST) {
            color = 0xFF00FF00; // Green
        } else if (stamina > JUMP_STAMINA_COST / 2) {
            color = 0xFFFFFF00; // Yellow
        } else {
            color = 0xFFFF0000; // Red
        }
        drawRect(x, y, x + fillWidth, y + barHeight, color);
        
        // Stamina text
        String staminaText = String.format("Stamina: %.0f/%.0f", stamina, MAX_STAMINA);
        mc.fontRendererObj.drawStringWithShadow(staminaText, x + barWidth / 2 - mc.fontRendererObj.getStringWidth(staminaText) / 2, y - 10, 0xFFFFFFFF);
        
        // Double jump status
        if (!hasDoubleJumped && !player.onGround) {
            mc.fontRendererObj.drawStringWithShadow("§aDouble Jump Available", x, y - 20, 0xFFFFFFFF);
        }
    }
    
    private void drawRect(int left, int top, int right, int bottom, int color) {
        Minecraft mc = Minecraft.getMinecraft();
        net.minecraft.client.gui.Gui.drawRect(left, top, right, bottom, color);
    }
}