package org.cyclops.integratedmekanism.part.aspect;

import mekanism.api.Coord4D;
import mekanism.api.chemical.ChemicalStack;
import mekanism.api.chemical.IChemicalHandler;
import mekanism.api.radiation.IRadiationManager;
import org.cyclops.integrateddynamics.api.part.aspect.IAspectRead;
import org.cyclops.integrateddynamics.core.evaluate.variable.*;
import org.cyclops.integrateddynamics.part.aspect.read.AspectReadBuilders;
import org.cyclops.integratedmekanism.Reference;
import org.cyclops.integratedmekanism.value.ValueObjectTypeChemicalStack;

/**
 * @author rubensworks
 */
public class MekanismAspects {

    public static final class Read {

        public static final class Chemical {

            public static final IAspectRead<ValueTypeBoolean.ValueBoolean, ValueTypeBoolean> BOOLEAN_FULL =
                    MekanismAspectReadBuilders.Chemical.BUILDER_BOOLEAN.handle(tankInfo -> {
                        boolean allFull = true;
                        for (int i = 0; i < tankInfo.getTanks(); i++) {
                            if (tankInfo.getChemicalInTank(i).isEmpty() && tankInfo.getTankCapacity(i) > 0
                                    || (!tankInfo.getChemicalInTank(i).isEmpty() && tankInfo.getChemicalInTank(i).getAmount() < tankInfo.getTankCapacity(i))) {
                                allFull = false;
                            }
                        }
                        return allFull;
                    }).handle(AspectReadBuilders.PROP_GET_BOOLEAN, "full").buildRead();
            public static final IAspectRead<ValueTypeBoolean.ValueBoolean, ValueTypeBoolean> BOOLEAN_EMPTY =
                    MekanismAspectReadBuilders.Chemical.BUILDER_BOOLEAN.handle(tankInfo -> {
                        for (int i = 0; i < tankInfo.getTanks(); i++) {
                            if (!tankInfo.getChemicalInTank(i).isEmpty() && tankInfo.getTankCapacity(i) > 0
                                    || (!tankInfo.getChemicalInTank(i).isEmpty() && tankInfo.getChemicalInTank(i).getAmount() < tankInfo.getTankCapacity(i))) {
                                return false;
                            }
                        }
                        return true;
                    }).handle(AspectReadBuilders.PROP_GET_BOOLEAN, "empty").buildRead();
            public static final IAspectRead<ValueTypeBoolean.ValueBoolean, ValueTypeBoolean> BOOLEAN_NONEMPTY =
                    MekanismAspectReadBuilders.Chemical.BUILDER_BOOLEAN.handle(tankInfo -> {
                        boolean hasChemical = false;
                        for (int i = 0; i < tankInfo.getTanks(); i++) {
                            if (!tankInfo.getChemicalInTank(i).isEmpty() && tankInfo.getChemicalInTank(i).getAmount() > 0) {
                                hasChemical = true;
                            }
                        }
                        return hasChemical;
                    }).handle(AspectReadBuilders.PROP_GET_BOOLEAN, "nonempty").buildRead();
            public static final IAspectRead<ValueTypeBoolean.ValueBoolean, ValueTypeBoolean> BOOLEAN_APPLICABLE =
                    MekanismAspectReadBuilders.Chemical.BUILDER_BOOLEAN.handle(
                            tankInfo -> tankInfo.getTanks() > 0
                    ).handle(AspectReadBuilders.PROP_GET_BOOLEAN, "applicable").buildRead();

            public static final IAspectRead<ValueTypeLong.ValueLong, ValueTypeLong> LONG_AMOUNT =
                    MekanismAspectReadBuilders.Chemical.BUILDER_LONG_ACTIVATABLE.handle(MekanismAspectReadBuilders.Chemical.PROP_GET_CHEMICALSTACK).handle(
                            ChemicalStack::getAmount
                    ).handle(AspectReadBuilders.PROP_GET_LONG, "amount").buildRead();
            public static final IAspectRead<ValueTypeLong.ValueLong, ValueTypeLong> LONG_AMOUNTTOTAL =
                    MekanismAspectReadBuilders.Chemical.BUILDER_LONG.handle(tankInfo -> {
                        long amount = 0;
                        for (int i = 0; i < tankInfo.getTanks(); i++) {
                            try {
                                amount = Math.addExact(amount, tankInfo.getChemicalInTank(i).getAmount());
                            } catch (ArithmeticException e) {
                                amount = Integer.MAX_VALUE;
                            }
                            if (amount == Integer.MAX_VALUE) {
                                break;
                            }
                        }
                        return amount;
                    }).handle(AspectReadBuilders.PROP_GET_LONG, "totalamount").buildRead();
            public static final IAspectRead<ValueTypeLong.ValueLong, ValueTypeLong> LONG_CAPACITY =
                    MekanismAspectReadBuilders.Chemical.BUILDER_LONG_ACTIVATABLE.handle(
                            tankInfo -> tankInfo != null ? tankInfo.getLeft().getTankCapacity(tankInfo.getRight()) : 0
                    ).handle(AspectReadBuilders.PROP_GET_LONG, "capacity").buildRead();
            public static final IAspectRead<ValueTypeLong.ValueLong, ValueTypeLong> LONG_CAPACITYTOTAL =
                    MekanismAspectReadBuilders.Chemical.BUILDER_LONG.handle(tankInfo -> {
                        long capacity = 0;
                        for (int i = 0; i < tankInfo.getTanks(); i++) {
                            try {
                                capacity = Math.addExact(capacity, tankInfo.getTankCapacity(i));
                            } catch (ArithmeticException e) {
                                capacity = Integer.MAX_VALUE;
                            }
                            if (capacity == Integer.MAX_VALUE) {
                                break;
                            }
                        }
                        return capacity;
                    }).handle(AspectReadBuilders.PROP_GET_LONG, "totalcapacity").buildRead();
            public static final IAspectRead<ValueTypeInteger.ValueInteger, ValueTypeInteger> INTEGER_TANKS =
                    MekanismAspectReadBuilders.Chemical.BUILDER_INTEGER.handle(
                            IChemicalHandler::getTanks
                    ).handle(AspectReadBuilders.PROP_GET_INTEGER, "tanks").buildRead();

            public static final IAspectRead<ValueTypeDouble.ValueDouble, ValueTypeDouble> DOUBLE_FILLRATIO =
                    MekanismAspectReadBuilders.Chemical.BUILDER_DOUBLE_ACTIVATABLE.handle(tankInfo -> {
                        if(tankInfo == null) {
                            return 0D;
                        }
                        double amount = tankInfo.getLeft().getChemicalInTank(tankInfo.getRight()).getAmount();
                        return amount / (double) tankInfo.getLeft().getTankCapacity(tankInfo.getRight());
                    }).handle(AspectReadBuilders.PROP_GET_DOUBLE, "fillratio").buildRead();

            public static final IAspectRead<ValueTypeList.ValueList, ValueTypeList> LIST_TANKCHEMICALS =
                    AspectReadBuilders.BUILDER_LIST.byMod(Reference.MOD_ID).appendKind("chemical").handle(MekanismAspectReadBuilders.Chemical.PROP_GET_LIST_CHEMICALSTACKS, "chemicalstacks").buildRead();
            public static final IAspectRead<ValueTypeList.ValueList, ValueTypeList> LIST_TANKCAPACITIES =
                    AspectReadBuilders.BUILDER_LIST.byMod(Reference.MOD_ID).appendKind("chemical").handle(MekanismAspectReadBuilders.Chemical.PROP_GET_LIST_CAPACITIES, "capacities").buildRead();

            public static final IAspectRead<ValueObjectTypeChemicalStack.ValueChemicalStack, ValueObjectTypeChemicalStack> CHEMICALSTACK =
                    MekanismAspectReadBuilders.BUILDER_OBJECT_CHEMICALSTACK
                            .handle(MekanismAspectReadBuilders.Chemical.PROP_GET_ACTIVATABLE, "chemical").withProperties(MekanismAspectReadBuilders.Chemical.PROPERTIES)
                            .handle(MekanismAspectReadBuilders.Chemical.PROP_GET_CHEMICALSTACK)
                            .handle(MekanismAspectReadBuilders.PROP_GET_CHEMICALSTACK)
                            .buildRead();

        }

        public static final class Machine {

            public static final IAspectRead<ValueTypeDouble.ValueDouble, ValueTypeDouble> DOUBLE_FISSIONREACTOR_DAMAGE =
                    MekanismAspectReadBuilders.Machine.BUILDER_FISSIONREACTOR_DOUBLE.handle(reactor -> reactor
                            .map(r -> r.reactorDamage)
                            .orElse(0D)
                    ).handle(AspectReadBuilders.PROP_GET_DOUBLE, "damage").buildRead();
            public static final IAspectRead<ValueTypeDouble.ValueDouble, ValueTypeDouble> DOUBLE_FISSIONREACTOR_BURNRATE =
                    MekanismAspectReadBuilders.Machine.BUILDER_FISSIONREACTOR_DOUBLE.handle(reactor -> reactor
                            .map(r -> r.lastBurnRate)
                            .orElse(0D)
                    ).handle(AspectReadBuilders.PROP_GET_DOUBLE, "burnrate").buildRead();
            public static final IAspectRead<ValueTypeDouble.ValueDouble, ValueTypeDouble> DOUBLE_FISSIONREACTOR_BURNRATEMAX =
                    MekanismAspectReadBuilders.Machine.BUILDER_FISSIONREACTOR_DOUBLE.handle(reactor -> reactor
                            .map(r -> (double) r.getMaxBurnRate())
                            .orElse(0D)
                    ).handle(AspectReadBuilders.PROP_GET_DOUBLE, "burnratemax").buildRead();
            public static final IAspectRead<ValueTypeDouble.ValueDouble, ValueTypeDouble> DOUBLE_FISSIONREACTOR_BURNRATELIMIT =
                    MekanismAspectReadBuilders.Machine.BUILDER_FISSIONREACTOR_DOUBLE.handle(reactor -> reactor
                            .map(r -> r.rateLimit)
                            .orElse(0D)
                    ).handle(AspectReadBuilders.PROP_GET_DOUBLE, "burnratelimit").buildRead();
            public static final IAspectRead<ValueTypeLong.ValueLong, ValueTypeLong> LONG_FISSIONREACTOR_HEATERATE =
                    MekanismAspectReadBuilders.Machine.BUILDER_FISSIONREACTOR_LONG.handle(reactor -> reactor
                            .map(r -> r.lastBoilRate)
                            .orElse(0L)
                    ).handle(AspectReadBuilders.PROP_GET_LONG, "heatingrate").buildRead();
            public static final IAspectRead<ValueTypeDouble.ValueDouble, ValueTypeDouble> DOUBLE_FISSIONREACTOR_HEATERATE =
                    MekanismAspectReadBuilders.Machine.BUILDER_FISSIONREACTOR_DOUBLE.handle(reactor -> reactor
                            .map(r -> r.lastEnvironmentLoss)
                            .orElse(0D)
                    ).handle(AspectReadBuilders.PROP_GET_DOUBLE, "environmentloss").buildRead();

        }

        public static final class World {

            public static final IAspectRead<ValueTypeDouble.ValueDouble, ValueTypeDouble> DOUBLE_RADIATION =
                    MekanismAspectReadBuilders.World.BUILDER_DOUBLE.handle(
                            dimPos -> IRadiationManager.INSTANCE.getRadiationLevel(
                                    new Coord4D(dimPos.getBlockPos().getX(), dimPos.getBlockPos().getY(), dimPos.getBlockPos().getZ(), dimPos.getLevelKey())
                            )
                    ).handle(AspectReadBuilders.PROP_GET_DOUBLE, "radiation").buildRead();

        }

    }

}
