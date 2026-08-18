# Bell Radar (bellrange) — Minecraft 26.1.2

Mod Fabric yang menambah "radar" raider/pillager di sekitar bell: setiap
kali bell dipukul atau diklik kanan, semua entity dari tag
`#minecraft:raiders` (pillager, vindicator, evoker, ravager, witch anggota
raid, dst) dalam radius custom (default **96 block**) akan diberi efek
Glowing, jadi kelihatan tembus tembok.

## Kenapa beda dari versi sebelumnya (bukan pakai Mixin lagi)?

Minecraft 26.1 (dirilis Maret 2026) mengubah total cara modding-nya:
game-nya sekarang **tidak diobfuscate lagi**, dan Fabric pindah dari mapping
Yarn ke mapping resmi Mojang. Karena ini kejadian setelah masa pelatihan
saya, saya tidak 100% yakin dengan nama method privat di dalam
`BellBlockEntity` untuk versi ini (yang dulu saya pakai lewat Mixin
`@ModifyConstant`).

Makanya untuk versi 26.1.2 ini saya pakai pendekatan yang jauh lebih aman:
**event Fabric API** (`AttackBlockCallback` & `UseBlockCallback`) yang
mendeteksi kapan bell dipukul/diklik, lalu logikanya kita tulis sendiri
dari nol pakai API publik yang stabil (tag entity, status effect, dll) —
tidak menyentuh kode internal bell sama sekali. Mekanisme bell bawaan
(radius 48 block) tetap jalan normal seperti biasa; mod ini cuma
menambahkan cakupan ekstra di atasnya.

## Cara build

1. Download template resmi terbaru: https://github.com/FabricMC/fabric-example-mod
   (pastikan kamu ambil branch/versi yang sudah support 26.1, karena
   template lama untuk versi obfuscated strukturnya beda).
2. Timpa file-file berikut dari project ini ke template:
   - `src/main/java/com/qayza/bellrange/BellRangeMod.java`
   - `src/main/resources/fabric.mod.json`
   - `gradle.properties`
   - `build.gradle`
3. Pastikan JDK 25 terpasang dan `JAVA_HOME` mengarah ke situ (26.1.2
   mewajibkan Java 25 minimum untuk Gradle).
4. Buka di IntelliJ IDEA (disarankan versi 2025.3+), Gradle sync.
5. `./gradlew build` — hasil jar ada di `build/libs/` (tidak ada lagi
   proses "remap" terpisah karena Minecraft sudah tidak diobfuscate).
6. Copy jar ke folder `mods`. Perlu **Fabric Loader ≥0.18.4** dan
   **Fabric API** (versi `0.146.1+26.1.2` atau yang sesuai) terpasang.

## Kalau ada error saat build

Karena ini rilis yang cukup baru, kombinasi versi Loom/Loader/Fabric API
yang tepat sebaiknya dicek ulang di https://fabricmc.net/develop —
tinggal pilih Minecraft 26.1.2 di sana untuk lihat versi terbaru yang
direkomendasikan saat kamu baca ini.

Kalau ada error terkait nama class/method (misalnya `cannot find symbol`),
kemungkinan API-nya berubah lagi di update berikutnya — cek nama yang
benar di https://mappings.dev atau lewat Linkie
(https://linkie.shedaniel.dev/mappings) dengan namespace "mojang".

## Ubah radius atau durasi glow

Edit dua konstanta di `BellRangeMod.java`:
- `BELL_DETECTION_RANGE` — radius pencarian raider (block)
- `GLOW_DURATION_TICKS` — lama efek glowing bertahan (20 tick = 1 detik)
