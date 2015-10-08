package com.rwtema.tinkertailor.render;

import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.Tessellator;

public class ModelBoxNoRender extends ModelBox {
    public ModelBoxNoRender(ModelRenderer p_i1171_1_, int p_i1171_2_, int p_i1171_3_, float p_i1171_4_, float p_i1171_5_, float p_i1171_6_, int p_i1171_7_, int p_i1171_8_, int p_i1171_9_, float p_i1171_10_) {
        super(p_i1171_1_, p_i1171_2_, p_i1171_3_, p_i1171_4_, p_i1171_5_, p_i1171_6_, p_i1171_7_, p_i1171_8_, p_i1171_9_, p_i1171_10_);
    }

    @Override
    public void render(Tessellator p_78245_1_, float p_78245_2_) {

    }
}
