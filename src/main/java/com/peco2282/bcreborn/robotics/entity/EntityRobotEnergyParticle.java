/*
 * BC Reborn
 *
 * Copyright (c) 2011-2017, SpaceToad and the BuildCraft Team
 * Copyright (c) 2025-2026 peco2282
 *
 * Contains original work and code derived from BuildCraft.
 *
 * Licensed under the Minecraft Mod Public License 1.0 (MMPL).
 * See LICENSE for details.
 */
package com.peco2282.bcreborn.robotics.entity;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class EntityRobotEnergyParticle extends TextureSheetParticle {
	private final float smokeParticleScale;

	public EntityRobotEnergyParticle(ClientLevel world, double x, double y, double z,
									 double vx,
									 double vy, double vz) {
		this(world, x, y, z, vx, vy, vz, 1.0F);
	}

	public EntityRobotEnergyParticle(ClientLevel world, double x, double y, double z,
									 double vx,
									 double vy, double vz, float size) {
		super(world, x, y, z, vx, vy, vz);
		this.xd *= 0.10000000149011612D;
		this.yd *= 0.10000000149011612D;
		this.zd *= 0.10000000149011612D;
		this.xd += vx;
		this.yd += vy;
		this.zd += vz;
		this.rCol = (float) (Math.random() * 0.6);
		this.gCol = 0;
		this.bCol = 0;
		this.quadSize *= 0.75F;
		this.quadSize *= size;
		this.smokeParticleScale = this.quadSize;
		this.lifetime = (int) (16.0D / (Math.random() * 0.8D + 0.2D));
		this.lifetime = (int) (this.lifetime * size);
		this.hasPhysics = true;
	}

	@Override
	public ParticleRenderType getRenderType() {
		return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
	}

	@Override
	public float getQuadSize(float partialTicks) {
		float f6 = (this.age + partialTicks) / this.lifetime * 32.0F;
		f6 = Mth.clamp(f6, 0.0F, 1.0F);
		return this.smokeParticleScale * f6;
	}

	@Override
	public void tick() {
		this.xo = this.x;
		this.yo = this.y;
		this.zo = this.z;

		if (this.age++ >= this.lifetime) {
			this.remove();
		}

		// this.setParticleTextureIndex(7 - this.age * 8 / this.lifetime);
		// 1.20.1 では ParticleOptions / SpritePicker を使うのが一般的だが、
		// 最小限の変更で済ませるためにここでは super.tick() を呼ぶ。
		// 本来は SpritePicker をコンストラクタで受け取り、ここで選択する。
		
		this.move(this.xd, this.yd, this.zd);

		this.xd *= 0.98;
		this.yd += 0.0005;
		this.zd *= 0.98;

		if (this.onGround) {
			this.xd *= 0.699999988079071D;
			this.zd *= 0.699999988079071D;
		}
	}
}
