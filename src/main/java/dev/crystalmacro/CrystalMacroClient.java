package dev.crystalmacro;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.boss.enderdragon.EndCrystal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

/**
 * Crystal-Macro (Minecraft 26.2, Mojang-Mappings).
 * Taste halten -> pro Client-Tick genau EINE Aktion: Kristall im Fadenkreuz
 * zerstoeren ODER Kristall auf Obsidian/Bedrock platzieren.
 * Vanilla-Reichweite (Minecraft#hitResult), max. 20 Aktionen/s = Server-Tickrate,
 * keine Sleeps, keine Extra-Threads, keine zusaetzlichen Pakete.
 */
public class CrystalMacroClient implements ClientModInitializer {

    private static KeyMapping holdKey;

    @Override
    public void onInitializeClient() {
        KeyMapping.Category category = KeyMapping.Category.register(
                Identifier.fromNamespaceAndPath("crystalmacro", "main"));
        holdKey = KeyMappingHelper.registerKeyMapping(new KeyMapping(
                "key.crystalmacro.hold",
                InputConstants.Type.KEYSYM,
                InputConstants.KEY_R,
                category));
        ClientTickEvents.END_CLIENT_TICK.register(CrystalMacroClient::tick);
    }

    private static void tick(Minecraft mc) {
        if (!holdKey.isDown()) return;
        if (mc.player == null || mc.level == null || mc.gameMode == null) return;
        if (mc.screen != null || mc.player.isUsingItem()) return;

        HitResult hit = mc.hitResult;
        if (hit == null) return;

        if (hit.getType() == HitResult.Type.ENTITY) {
            Entity target = ((EntityHitResult) hit).getEntity();
            if (target instanceof EndCrystal) {
                mc.gameMode.attack(mc.player, target);
                mc.player.swing(InteractionHand.MAIN_HAND);
            }
            return;
        }

        if (hit.getType() == HitResult.Type.BLOCK) {
            InteractionHand hand = crystalHand(mc.player);
            if (hand == null) return;

            BlockHitResult bhr = (BlockHitResult) hit;
            BlockPos base = bhr.getBlockPos();
            var baseState = mc.level.getBlockState(base);
            if (!baseState.is(Blocks.OBSIDIAN) && !baseState.is(Blocks.BEDROCK)) return;

            BlockPos above = base.above();
            if (!mc.level.getBlockState(above).isAir()) return;
            AABB space = new AABB(above.getX(), above.getY(), above.getZ(),
                    above.getX() + 1, above.getY() + 2, above.getZ() + 1);
            if (!mc.level.getEntities((Entity) null, space).isEmpty()) return; // Platz belegt -> kein unnoetiges Paket

            InteractionResult result = mc.gameMode.useItemOn(mc.player, hand, bhr);
            if (result.consumesAction()) mc.player.swing(hand);
        }
    }

    private static InteractionHand crystalHand(Player p) {
        if (p.getMainHandItem().is(Items.END_CRYSTAL)) return InteractionHand.MAIN_HAND;
        if (p.getOffhandItem().is(Items.END_CRYSTAL)) return InteractionHand.OFF_HAND;
        return null;
    }
}
