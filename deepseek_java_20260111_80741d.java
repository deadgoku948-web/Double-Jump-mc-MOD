package com.example.doublejump;

import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;

@SideOnly(Side.CLIENT)
public class ClientProxy extends CommonProxy {
    
    public static KeyBinding rechargeKey;
    
    @Override
    public void preInit(FMLPreInitializationEvent event) {
        super.preInit(event);
    }
    
    @Override
    public void init(FMLInitializationEvent event) {
        super.init(event);
        
        // Register key bindings
        rechargeKey = new KeyBinding("Recharge Stamina", Keyboard.KEY_R, "Double Jump Mod");
        ClientRegistry.registerKeyBinding(rechargeKey);
    }
}