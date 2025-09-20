package org.cyclops.integratedmekanism.network.packet;

import mekanism.api.chemical.ChemicalStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fluids.FluidStack;
import org.cyclops.cyclopscore.network.CodecField;
import org.cyclops.cyclopscore.network.PacketCodec;
import org.cyclops.integrateddynamics.core.ingredient.ItemMatchProperties;
import org.cyclops.integrateddynamics.inventory.container.ContainerLogicProgrammerBase;
import org.cyclops.integratedmekanism.ingredient.MekanismIngredientComponents;
import org.cyclops.integratedmekanism.logicprogrammer.ValueTypeRecipeChemicalLPElement;

import java.util.List;

/**
 * @author rubensworks
 */
public class CPacketValueTypeRecipeChemicalLPElementSetRecipe extends PacketCodec {

    static {
        PacketCodec.addCodedAction(ChemicalStack.class, new ICodecAction() {
            @Override
            public void encode(Object object, FriendlyByteBuf output) {
                CompoundTag tag = new CompoundTag();
                tag.put("i", MekanismIngredientComponents.INGREDIENT_CHEMICALSTACK.getSerializer().serializeInstance((ChemicalStack<?>) object));
                output.writeNbt(tag);
            }

            @Override
            public Object decode(FriendlyByteBuf input) {
                return MekanismIngredientComponents.INGREDIENT_CHEMICALSTACK.getSerializer().deserializeInstance(input.readNbt().get("i"));
            }
        });
    }

    @CodecField
    private int windowId;
    @CodecField
    private List<ItemMatchProperties> itemInputs;
    @CodecField
    private List<FluidStack> fluidInputs;
    @CodecField
    private List<ChemicalStack<?>> chemicalInputs;
    @CodecField
    private List<ItemStack> itemOutputs;
    @CodecField
    private List<FluidStack> fluidOutputs;
    @CodecField
    private List<ChemicalStack<?>> chemicalOutputs;

    public CPacketValueTypeRecipeChemicalLPElementSetRecipe() {

    }

    public CPacketValueTypeRecipeChemicalLPElementSetRecipe(int windowId, List<ItemMatchProperties> itemInputs, List<FluidStack> fluidInputs, List<ChemicalStack<?>> chemicalInputs,
                                                            List<ItemStack> itemOutputs, List<FluidStack> fluidOutputs, List<ChemicalStack<?>> chemicalOutputs) {
        this.windowId = windowId;
        this.itemInputs = itemInputs;
        this.fluidInputs = fluidInputs;
        this.chemicalInputs = chemicalInputs;
        this.itemOutputs = itemOutputs;
        this.fluidOutputs = fluidOutputs;
        this.chemicalOutputs = chemicalOutputs;
    }

    @Override
    public boolean isAsync() {
        return false;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void actionClient(Level world, Player player) {

    }

    @Override
    public void actionServer(Level world, ServerPlayer player) {
        if(player.containerMenu.containerId == windowId) {
            ContainerLogicProgrammerBase container = (ContainerLogicProgrammerBase) player.containerMenu;
            ValueTypeRecipeChemicalLPElement element = (ValueTypeRecipeChemicalLPElement) container.getActiveElement();
            element.setRecipeGrid(container, itemInputs, fluidInputs, chemicalInputs, itemOutputs, fluidOutputs, chemicalOutputs);
        }
    }

}
