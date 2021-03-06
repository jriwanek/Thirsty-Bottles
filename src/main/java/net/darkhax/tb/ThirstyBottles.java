package net.darkhax.tb;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemGlassBottle;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock;
import net.minecraftforge.fluids.IFluidBlock;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLFingerprintViolationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod(modid = "thirstybottles", name = "Thirsty Bottles", version = "@VERSION@", certificateFingerprint = "@FINGERPRINT@")
public class ThirstyBottles {

    private static final Logger LOG = LogManager.getLogger("Thirsty Bottles");
    
	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {

		MinecraftForge.EVENT_BUS.register(this);
	}

	@SubscribeEvent
	public void onItemUsed(RightClickBlock event) {

		if (event.getWorld().isRemote)
			return;
		
		if (event.getItemStack() != null && event.getItemStack().getItem() instanceof ItemGlassBottle) {

			BlockPos pos = new BlockPos(event.getHitVec());
			IBlockState state = event.getWorld().getBlockState(pos);
			EntityPlayer player = event.getEntityPlayer();

			if (state == null) 
				return;
			
			if (state.getMaterial() == Material.WATER && (state.getBlock() instanceof IFluidBlock || state.getBlock() instanceof BlockLiquid) && Blocks.WATER.canCollideCheck(state, true)) {
				
				event.getWorld().playSound(player, player.posX, player.posY, player.posZ, SoundEvents.ITEM_BOTTLE_FILL, SoundCategory.NEUTRAL, 1.0F, 1.0F);
				event.getEntityPlayer().setHeldItem(event.getHand(), transformBottle(event.getItemStack(), event.getEntityPlayer(), new ItemStack(Items.POTIONITEM)));
				event.getWorld().setBlockToAir(pos);
			}
		}
	}

	private ItemStack transformBottle(ItemStack input, EntityPlayer player, ItemStack stack) {
		
		input.shrink(1);
		player.addStat(StatList.getObjectUseStats(input.getItem()));

		if (input.getCount() < 1) {
			
			return stack;
		} 
		
		else {
			
			if (!player.inventory.addItemStackToInventory(stack)) {
				
				player.dropItem(stack, false);
			}

			return input;
		}
	}
	
    
    @EventHandler
    public void onFingerprintViolation (FMLFingerprintViolationEvent event) {

        LOG.error("Invalid fingerprint detected! The file " + event.getSource().getName() + " may have been tampered with. This version will NOT be supported by the author!");
    }
}