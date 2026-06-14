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
    // アイテムとして見やすいように中央付近に配置
    poseStack.translate(0.0, 0.0, 0.5 - thickness / 2.0);
    poseStack.scale(1.0f, 1.0f, thickness);

    // renderSingleBlockを使用すると、ブロックの全面が描画される
    dispatcher.renderSingleBlock(state, poseStack, buffer, packedLight, packedOverlay, ModelData.EMPTY, net.minecraft.client.renderer.RenderType.cutout());

    poseStack.popPose();
  }
}
