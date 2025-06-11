package com.github.Book0225.enemies_with_guns;

import com.mojang.logging.LogUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.slf4j.Logger;

// Moddingに必要なMinecraftやForgeのクラスをインポート
import net.minecraft.world.item.Item;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent; // パッケージ名を修正

// 自作Modのクラスをインポート
import com.github.Book0225.enemies_with_guns.item.AssaultRifleItem;
import com.github.Book0225.enemies_with_guns.entity.projectile.CustomBulletEntity;
import com.github.Book0225.enemies_with_guns.entity.mob.GuerrillaSoldierEntity;
import com.github.Book0225.enemies_with_guns.client.renderer.entity.CustomBulletRenderer;
import com.github.Book0225.enemies_with_guns.client.renderer.entity.GuerrillaSoldierRenderer;


// Modのメインクラス。@ModアノテーションでModIDを指定。
@Mod(Enemies_with_guns.MODID)
public class Enemies_with_guns {

    // ModのIDを定義（必須）
    public static final String MODID = "enemies_with_guns";
    // ロガー（デバッグメッセージ出力用）
    static final Logger LOGGER = LogUtils.getLogger();

    // DeferredRegisterを使って、Modのアイテムとエンティティを登録
    // アイテムのレジストリ
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);
    // エンティティタイプのレジストリ
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, MODID);

    // 銃アイテムの登録
    public static final RegistryObject<Item> ASSAULT_RIFLE = ITEMS.register("assault_rifle",
            () -> new AssaultRifleItem(new Item.Properties().stacksTo(1))); // スタックしないアイテムとして設定

    // 弾丸エンティティタイプの登録
    public static final RegistryObject<EntityType<CustomBulletEntity>> CUSTOM_BULLET = ENTITIES.register("custom_bullet",
            () -> EntityType.Builder.<CustomBulletEntity>of(CustomBulletEntity::new, MobCategory.MISC) // MOBカテゴリーはMISC（その他）
                    .sized(0.1F, 0.1F) // ヒットボックスのサイズ
                    .build(MODID + ":custom_bullet")); // ビルド時の名前。modid:name とするのが慣習

    // ゲリラ兵Mobエンティティタイプの登録
    public static final RegistryObject<EntityType<GuerrillaSoldierEntity>> GUERRILLA_SOLDIER = ENTITIES.register("guerrilla_soldier",
            () -> EntityType.Builder.of(GuerrillaSoldierEntity::new, MobCategory.MONSTER) // モンスターカテゴリ
                    .sized(0.6F, 1.95F) // Mobのヒットボックスサイズ（プレイヤーと同じくらい）
                    .build(MODID + ":guerrilla_soldier"));


    // Modのコンストラクタ
    public Enemies_with_guns() {
        // Modイベントバスを取得
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        // Modの初期化フェーズのイベントリスナーを登録
        modEventBus.addListener(this::commonSetup);

        // アイテムとエンティティのレジストリをModイベントバスに登録
        ITEMS.register(modEventBus);
        ENTITIES.register(modEventBus);

        // エンティティ属性登録イベントをModイベントバスに登録
        modEventBus.addListener(Enemies_with_guns::onEntityAttributeCreation); // staticメソッドなのでクラス::メソッド名で参照

        // Forgeのメインイベントバスに、サーバー開始イベントなどを登録
        MinecraftForge.EVENT_BUS.register(this);
    }

    // commonSetupイベントハンドラ（サーバー・クライアント共通の初期化処理）
    private void commonSetup(final FMLCommonSetupEvent event) {
        LOGGER.info("HELLO FROM COMMON SETUP");
        LOGGER.info("DIRT BLOCK >> {}", ForgeRegistries.BLOCKS.getKey(Blocks.DIRT));
        // ここに初期化処理を追加
    }

    // Mobの属性（体力、移動速度など）を登録するためのイベントリスナー
    @SubscribeEvent
    public static void onEntityAttributeCreation(EntityAttributeCreationEvent event) { // パッケージ名を修正
        event.put(Enemies_with_guns.GUERRILLA_SOLDIER.get(), GuerrillaSoldierEntity.createAttributes().build());
    }

    // サーバー開始イベントハンドラ
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        LOGGER.info("HELLO from server starting");
    }

    // クライアント側でのMod初期化処理を行う内部クラス
    // @Mod.EventBusSubscriberでModイベントバスに自動的に登録
    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {

        // クライアントセットアップイベントハンドラ
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            LOGGER.info("HELLO FROM CLIENT SETUP");
            LOGGER.info("MINECRAFT NAME >> {}", Minecraft.getInstance().getUser().getName());

            // エンティティのレンダラーを登録
            EntityRenderers.register(Enemies_with_guns.CUSTOM_BULLET.get(), CustomBulletRenderer::new);
            EntityRenderers.register(Enemies_with_guns.GUERRILLA_SOLDIER.get(), GuerrillaSoldierRenderer::new);
        }
    }
}