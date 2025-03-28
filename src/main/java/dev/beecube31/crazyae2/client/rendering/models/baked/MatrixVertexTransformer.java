package dev.beecube31.crazyae2.client.rendering.models.baked;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.renderer.vertex.VertexFormatElement;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.client.model.pipeline.QuadGatheringTransformer;

import javax.vecmath.Matrix4f;
import javax.vecmath.Vector4f;

final class MatrixVertexTransformer extends QuadGatheringTransformer {

    private final Matrix4f transform;

    public MatrixVertexTransformer(Matrix4f transform) {
        this.transform = transform;
    }

    @Override
    protected void processQuad() {
        VertexFormat format = this.parent.getVertexFormat();
        int count = format.getElementCount();

        for (int v = 0; v < 4; v++) {
            for (int e = 0; e < count; e++) {
                VertexFormatElement element = format.getElement(e);
                if (element.getUsage() == VertexFormatElement.EnumUsage.POSITION) {
                    this.parent.put(e, this.transform(this.quadData[e][v], element.getElementCount()));
                } else if (element.getUsage() == VertexFormatElement.EnumUsage.NORMAL) {
                    this.parent.put(e, this.transformNormal(this.quadData[e][v]));
                } else {
                    this.parent.put(e, this.quadData[e][v]);
                }
            }
        }
    }

    @Override
    public void setQuadTint(int tint) {
        this.parent.setQuadTint(tint);
    }

    @Override
    public void setQuadOrientation(EnumFacing orientation) {
        this.parent.setQuadOrientation(orientation);
    }

    @Override
    public void setApplyDiffuseLighting(boolean diffuse) {
        this.parent.setApplyDiffuseLighting(diffuse);
    }

    @Override
    public void setTexture(TextureAtlasSprite texture) {
        this.parent.setTexture(texture);
    }

    private float[] transform(float[] fs, int elemCount) {
        switch (fs.length) {
            case 3:
                javax.vecmath.Vector3f vec = new javax.vecmath.Vector3f(fs[0], fs[1], fs[2]);
                vec.x -= 0.5f;
                vec.y -= 0.5f;
                vec.z -= 0.5f;
                this.transform.transform(vec);
                vec.x += 0.5f;
                vec.y += 0.5f;
                vec.z += 0.5f;
                return new float[]{
                        vec.x,
                        vec.y,
                        vec.z
                };
            case 4:
                Vector4f vecc = new Vector4f(fs[0], fs[1], fs[2], fs[3]);
                // Otherwise all translation is lost
                if (elemCount == 3) {
                    vecc.w = 1;
                }
                vecc.x -= 0.5f;
                vecc.y -= 0.5f;
                vecc.z -= 0.5f;
                this.transform.transform(vecc);
                vecc.x += 0.5f;
                vecc.y += 0.5f;
                vecc.z += 0.5f;
                return new float[]{
                        vecc.x,
                        vecc.y,
                        vecc.z,
                        vecc.w
                };

            default:
                return fs;
        }
    }

    private float[] transformNormal(float[] fs) {
        Vector4f normal;

        switch (fs.length) {
            case 3:
                normal = new Vector4f(fs[0], fs[1], fs[2], 0);
                this.transform.transform(normal);
                normal.normalize();
                return new float[]{
                        normal.x,
                        normal.y,
                        normal.z
                };

            case 4:
                normal = new Vector4f(fs[0], fs[1], fs[2], fs[3]);
                this.transform.transform(normal);
                normal.normalize();
                return new float[]{
                        normal.x,
                        normal.y,
                        normal.z,
                        normal.w
                };

            default:
                return fs;
        }
    }
}
