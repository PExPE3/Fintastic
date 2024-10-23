package net.voidarkana.fintastic.common.item.custom;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.voidarkana.fintastic.common.item.YAFMItems;

import java.util.function.Supplier;

public class FullFishnetItem extends ForgeSpawnEggItem {

    public FullFishnetItem(Supplier<? extends EntityType<? extends Mob>> type, Properties props) {
        super(type, 0xffffff, 0xffffff, props);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        context.getLevel().playSound(null, context.getClickedPos(), SoundEvents.FISHING_BOBBER_RETRIEVE, SoundSource.AMBIENT, 1, 1);
        context.getPlayer().setItemInHand(context.getHand(), new ItemStack(YAFMItems.FISHNET.get()));

        return super.useOn(context);
    }


}
