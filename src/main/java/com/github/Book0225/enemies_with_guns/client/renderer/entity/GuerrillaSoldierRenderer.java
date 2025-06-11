package com.github.Book0225.enemies_with_guns.client.renderer.entity;

import com.github.Book0225.enemies_with_guns.Enemies_with_guns;
import com.github.Book0225.enemies_with_guns.entity.mob.GuerrillaSoldierEntity;

import net.minecraft.client.model.HumanoidModel; // ヒューマノイドモデル（ゾンビ、スケルトンと同じ人型）
import net.minecraft.client.model.geom.ModelLayers; // モデルレイヤー（Minecraftの既存モデルを使う）
import net.minecraft.client.renderer.entity.HumanoidMobRenderer; // ヒューマノイドMobのレンダラー
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class GuerrillaSoldierRenderer extends HumanoidMobRenderer<GuerrillaSoldierEntity, HumanoidModel<GuerrillaSoldierEntity>> {
    // ゲリラ兵のテクスチャを定義
    public static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(Enemies_with_guns.MODID, "textures/entity/mob/guerrilla_soldier.png");

    public GuerrillaSoldierRenderer(EntityRendererProvider.Context context) {
        // HumanoidMobRendererのコンストラクタを呼び出す
        // context: レンダラーのコンテキスト
        // new HumanoidModel<>(context.bakeLayer(ModelLayers.ZOMBIE)): ゾンビのモデルを流用
        // 0.5F: Mobの影のサイズ
        super(context, new HumanoidModel<>(context.bakeLayer(ModelLayers.ZOMBIE)), 0.5F);
    }

    // ゲリラ兵のテクスチャを返す
    @Override
    public ResourceLocation getTextureLocation(GuerrillaSoldierEntity entity) {
        return TEXTURE;
    }
}