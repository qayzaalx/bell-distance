package com.qayza.bellrange;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BellRangeMod implements ModInitializer {

    public static final String MOD_ID = "bellrange";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    /**
     * Radius pencarian raider/pillager saat bell dibunyikan, dalam block.
     * Vanilla defaultnya cuma 48 (dan hanya glow, tidak ada radius pencarian
     * terpisah). Ganti angka ini sesuka kamu, misal 96 atau 128.
     */
    public static final int BELL_DETECTION_RANGE = 96;

    /** Berapa lama efek glowing bertahan (dalam tick). Vanilla: 60 tick (3 detik). */
    public static final int GLOW_DURATION_TICKS = 100;

    @Override
    public void onInitialize() {
        LOGGER.info("[Bell Radar] Radius deteksi bell diset ke {} block", BELL_DETECTION_RANGE);

        // Bell dibunyikan lewat klik kanan...
        UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> {
            if (world.getBlockState(hitResult.getBlockPos()).is(Blocks.BELL)) {
                revealNearbyRaiders(world, hitResult.getBlockPos());
            }
            return net.minecraft.world.InteractionResult.PASS;
        });

        // ...atau lewat dipukul (klik kiri).
        AttackBlockCallback.EVENT.register((player, world, hand, pos, direction) -> {
            if (world.getBlockState(pos).is(Blocks.BELL)) {
                revealNearbyRaiders(world, pos);
            }
            return net.minecraft.world.InteractionResult.PASS;
        });
    }

    /**
     * Mencari semua raider (pillager, vindicator, evoker, ravager, witch anggota raid, dll)
     * di sekitar posisi bell dalam radius BELL_DETECTION_RANGE, lalu memberi efek Glowing
     * supaya kelihatan lewat tembok. Ini logika kita sendiri, terpisah dari mekanisme
     * bawaan bell (yang tetap jalan seperti biasa dalam radius 48 block).
     */
    private void revealNearbyRaiders(Level world, BlockPos bellPos) {
        // Hanya jalankan di server side supaya efek benar-benar tersimpan/tersinkron.
        if (!(world instanceof ServerLevel serverLevel)) {
            return;
        }

        AABB searchBox = new AABB(bellPos).inflate(BELL_DETECTION_RANGE);

        serverLevel.getEntitiesOfClass(
                LivingEntity.class,
                searchBox,
                entity -> entity.getType().is(EntityTypeTags.RAIDERS)
        ).forEach(raider -> raider.addEffect(new MobEffectInstance(MobEffects.GLOWING, GLOW_DURATION_TICKS)));
    }
}
