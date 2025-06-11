package com.github.Book0225.enemies_with_guns.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.entity.ArrowRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation; // ResourceLocationをインポート
import com.github.Book0225.enemies_with_guns.Enemies_with_guns;
import com.github.Book0225.enemies_with_guns.entity.projectile.CustomBulletEntity;

public class CustomBulletRenderer extends ArrowRenderer<CustomBulletEntity> {
    // 弾丸のテクスチャを定義 (推奨された ResourceLocation.fromNamespaceAndPath() を使用)
    public static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(Enemies_with_guns.MODID, "textures/entity/projectile/bullet.png");

    public CustomBulletRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    // 弾丸のテクスチャを返す
    @Override
    public ResourceLocation getTextureLocation(CustomBulletEntity entity) {
        return TEXTURE;
    }
}