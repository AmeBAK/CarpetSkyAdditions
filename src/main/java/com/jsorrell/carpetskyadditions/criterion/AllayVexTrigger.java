package com.jsorrell.carpetskyadditions.criterion;

import com.google.gson.JsonObject;
import com.jsorrell.carpetskyadditions.util.SkyAdditionsResourceLocation;
import net.minecraft.advancements.critereon.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.animal.allay.Allay;
import net.minecraft.world.entity.monster.Vex;
import net.minecraft.world.level.storage.loot.LootContext;

public class AllayVexTrigger extends SimpleCriterionTrigger<AllayVexTrigger.Conditions> {
    static final ResourceLocation ID = new SkyAdditionsResourceLocation("allay_vex");

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    public void trigger(ServerPlayer player, Vex vex, Allay allay) {
        LootContext vexLootContext = EntityPredicate.createContext(player, vex);
        LootContext allayLootContext = EntityPredicate.createContext(player, allay);
        trigger(player, conditions -> conditions.matches(vexLootContext, allayLootContext));
    }

    @Override
    public Conditions createInstance(
            JsonObject json, EntityPredicate.Composite player, DeserializationContext context) {
        EntityPredicate.Composite vexPredicate = EntityPredicate.Composite.fromJson(json, "vex", context);
        EntityPredicate.Composite allayPredicate = EntityPredicate.Composite.fromJson(json, "allay", context);
        return new Conditions(player, vexPredicate, allayPredicate);
    }

    public static class Conditions extends AbstractCriterionTriggerInstance {
        private final EntityPredicate.Composite vex;
        private final EntityPredicate.Composite allay;

        public Conditions(
                EntityPredicate.Composite player, EntityPredicate.Composite vex, EntityPredicate.Composite allay) {
            super(ID, player);
            this.vex = vex;
            this.allay = allay;
        }

        public boolean matches(LootContext vexContext, LootContext allayContext) {
            return vex.matches(vexContext) && allay.matches(allayContext);
        }

        @Override
        public JsonObject serializeToJson(SerializationContext context) {
            JsonObject jsonObject = super.serializeToJson(context);
            jsonObject.add("vex", vex.toJson(context));
            jsonObject.add("allay", allay.toJson(context));
            return jsonObject;
        }
    }
}
