package com.jsorrell.carpetskyadditions.criterion;

import com.google.gson.JsonObject;
import com.jsorrell.carpetskyadditions.util.SkyAdditionsResourceLocation;
import net.minecraft.advancements.critereon.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.monster.CaveSpider;
import net.minecraft.world.entity.monster.Spider;
import net.minecraft.world.level.storage.loot.LootContext;

public class ConvertSpiderTrigger extends SimpleCriterionTrigger<ConvertSpiderTrigger.Conditions> {
    static final ResourceLocation ID = new SkyAdditionsResourceLocation("convert_spider");

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    public void trigger(ServerPlayer player, Spider spider, CaveSpider caveSpider) {
        LootContext spiderLootContext = EntityPredicate.createContext(player, spider);
        LootContext caveSpiderLootContext = EntityPredicate.createContext(player, caveSpider);
        trigger(player, conditions -> conditions.matches(spiderLootContext, caveSpiderLootContext));
    }

    @Override
    public Conditions createInstance(
            JsonObject json, EntityPredicate.Composite player, DeserializationContext context) {
        EntityPredicate.Composite spiderPredicate = EntityPredicate.Composite.fromJson(json, "spider", context);
        EntityPredicate.Composite caveSpiderPredicate =
                EntityPredicate.Composite.fromJson(json, "cave_spider", context);
        return new Conditions(player, spiderPredicate, caveSpiderPredicate);
    }

    public static class Conditions extends AbstractCriterionTriggerInstance {
        private final EntityPredicate.Composite spider;
        private final EntityPredicate.Composite caveSpider;

        public Conditions(
                EntityPredicate.Composite player,
                EntityPredicate.Composite spider,
                EntityPredicate.Composite caveSpider) {
            super(ID, player);
            this.spider = spider;
            this.caveSpider = caveSpider;
        }

        public boolean matches(LootContext spiderContext, LootContext caveSpiderContext) {
            return spider.matches(spiderContext) && caveSpider.matches(caveSpiderContext);
        }

        @Override
        public JsonObject serializeToJson(SerializationContext context) {
            JsonObject jsonObject = super.serializeToJson(context);
            jsonObject.add("spider", spider.toJson(context));
            jsonObject.add("cave_spider", caveSpider.toJson(context));
            return jsonObject;
        }
    }
}
