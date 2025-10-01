package org.cyclops.integratedmekanism.network.packet;

import mekanism.api.chemical.ChemicalStack;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.fluids.FluidStack;
import org.cyclops.cyclopscore.network.CodecField;
import org.cyclops.cyclopscore.network.PacketCodec;
import org.cyclops.cyclopscore.network.PacketCodecs;
import org.cyclops.integrateddynamics.core.ingredient.ItemMatchProperties;
import org.cyclops.integrateddynamics.inventory.container.ContainerLogicProgrammerBase;
import org.cyclops.integratedmekanism.Reference;
import org.cyclops.integratedmekanism.logicprogrammer.ValueTypeRecipeChemicalLPElement;

import java.util.List;

/**
 * @author rubensworks
 */
public class CPacketValueTypeRecipeChemicalLPElementSetRecipe extends PacketCodec<CPacketValueTypeRecipeChemicalLPElementSetRecipe> {

    public static final Type<CPacketValueTypeRecipeChemicalLPElementSetRecipe> ID = new Type<>(ResourceLocation.fromNamespaceAndPath(Reference.MOD_ID, "valuetype_recipe_chemical_lp_element_set_recipe"));
    public static final StreamCodec<RegistryFriendlyByteBuf, CPacketValueTypeRecipeChemicalLPElementSetRecipe> CODEC = getCodec(CPacketValueTypeRecipeChemicalLPElementSetRecipe::new);

    static {
        PacketCodecs.addCodedAction(ChemicalStack.class, new ICodecAction() {
            @Override
            public void encode(Object object, RegistryFriendlyByteBuf registryFriendlyByteBuf) {
                ChemicalStack.OPTIONAL_STREAM_CODEC.encode(registryFriendlyByteBuf, (ChemicalStack)object);
            }

            @Override
            public Object decode(RegistryFriendlyByteBuf input) {
                return ChemicalStack.OPTIONAL_STREAM_CODEC.decode(input);
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
    private List<ChemicalStack> chemicalInputs;
    @CodecField
    private List<ItemStack> itemOutputs;
    @CodecField
    private List<FluidStack> fluidOutputs;
    @CodecField
    private List<ChemicalStack> chemicalOutputs;

    public CPacketValueTypeRecipeChemicalLPElementSetRecipe() {
        super(ID);
    }

    public CPacketValueTypeRecipeChemicalLPElementSetRecipe(int windowId, List<ItemMatchProperties> itemInputs, List<FluidStack> fluidInputs, List<ChemicalStack> chemicalInputs,
                                                            List<ItemStack> itemOutputs, List<FluidStack> fluidOutputs, List<ChemicalStack> chemicalOutputs) {
        super(ID);
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
