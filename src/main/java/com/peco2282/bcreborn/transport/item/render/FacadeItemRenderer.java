package com.peco2282.bcreborn.transport.item.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.peco2282.bcreborn.api.facades.IFacadeItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.ModelData;

import java.util.List;

public class FacadeItemRenderer extends BlockEntityWithoutLevelRenderer {
  public static final FacadeItemRenderer INSTANCE = new FacadeItemRenderer();

  private FacadeItemRenderer() {
    super(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels());
  }

  @Override
  public void renderByItem(ItemStack stack, ItemDisplayContext displayContext, PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay) {
    if (!(stack.getItem() instanceof IFacadeItem facadeItem)) {
      return;
    }

    List<BlockState> states = facadeItem.getBlockStatesForFacade(stack);
    if (states.isEmpty()) {
      return;
    }

    BlockState state = states.get(0);
    BlockRenderDispatcher dispatcher = Minecraft.getInstance().getBlockRenderer();

    poseStack.pushPose();

    float thickness = 1.0f / 16.0f;
    boolean hollow = stack.hasTag() && stack.getOrCreateTag().getBoolean("hollow");

    // アイテムとして見やすいように中央付近に配置
    poseStack.translate(0.0, 0.0, 0.5 - thickness / 2.0);
    poseStack.scale(1.0f, 1.0f, thickness);

    if (hollow) {
      renderHollow(state, poseStack, buffer, packedLight, packedOverlay, dispatcher);
    } else {
      dispatcher.renderSingleBlock(state, poseStack, buffer, packedLight, packedOverlay, ModelData.EMPTY, net.minecraft.client.renderer.RenderType.cutout());
    }

    poseStack.popPose();
  }

  private void renderHollow(BlockState state, PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay, BlockRenderDispatcher dispatcher) {
    float holeMin = 4.0f / 16.0f;
    float holeMax = 12.0f / 16.0f;

    // 1. 上
    poseStack.pushPose();
    renderSection(state, 0, holeMax, 1.0f, 1.0f, poseStack, buffer, packedLight, packedOverlay, dispatcher);
    poseStack.popPose();

    // 2. 下
    poseStack.pushPose();
    renderSection(state, 0, 0, 1.0f, holeMin, poseStack, buffer, packedLight, packedOverlay, dispatcher);
    poseStack.popPose();

    // 3. 左
    poseStack.pushPose();
    renderSection(state, 0, holeMin, holeMin, holeMax, poseStack, buffer, packedLight, packedOverlay, dispatcher);
    poseStack.popPose();

    // 4. 右
    poseStack.pushPose();
    renderSection(state, holeMax, holeMin, 1.0f, holeMax, poseStack, buffer, packedLight, packedOverlay, dispatcher);
    poseStack.popPose();
  }

  private void renderSection(BlockState state, float xMin, float yMin, float xMax, float yMax, PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay, BlockRenderDispatcher dispatcher) {
    poseStack.translate(xMin, yMin, 0);
    poseStack.scale(xMax - xMin, yMax - yMin, 1.0f);
    dispatcher.renderSingleBlock(state, poseStack, buffer, packedLight, packedOverlay, ModelData.EMPTY, net.minecraft.client.renderer.RenderType.cutout());
  }
}
