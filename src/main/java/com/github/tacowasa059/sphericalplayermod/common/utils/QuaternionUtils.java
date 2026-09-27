package com.github.tacowasa059.sphericalplayermod.common.utils;

import com.github.tacowasa059.sphericalplayermod.common.Interface.ICustomPlayerData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.joml.AxisAngle4f;
import org.joml.Math;
import org.joml.Quaternionf;

public class QuaternionUtils {
    public static Quaternionf getUpdatedQuaternion(Vec3 currentPosition, Vec3 prevPosition, ICustomPlayerData playerData) {
        if(prevPosition == null) {
            return null;
        }

        Vec3 movement = currentPosition.subtract(prevPosition);
        double distance = movement.length();
        movement.subtract(0, movement.y(),0);// y方向をつぶす


        float radius = playerData.sphericalPlayerMod$getSize()/2.0f;

        Quaternionf quaternion = playerData.sphericalPlayerMod$getQuaternion();
        if(quaternion.equals(new Quaternionf(0,0,0,0))){
            quaternion = new Quaternionf(0,0,0,1);
        }

        // 移動方向に対するロール回転を計算
        double angle = distance/radius;

        Vec3 axis_D = movement.yRot((float) (Math.PI/2.0));
        axis_D = axis_D.normalize();

        if(axis_D.equals(Vec3.ZERO)) return null;

        Vec3 axis = new Vec3((float) axis_D.x, 0, (float) axis_D.z);

        Quaternionf rot_Quaternion = new Quaternionf(new AxisAngle4f((float) angle, (float) axis.x, (float) axis.y, (float) axis.z));

        rot_Quaternion = normalize(rot_Quaternion);

        rot_Quaternion.mul(quaternion);

        rot_Quaternion = normalize(rot_Quaternion);
        return rot_Quaternion;
    }

    private static Quaternionf normalize(Quaternionf quaternion) {
        float lvt_1_1_ = quaternion.x * quaternion.x + quaternion.y * quaternion.y
                + quaternion.z * quaternion.z + quaternion.w() * quaternion.w;
        if (lvt_1_1_ > 1.0E-6F) {
            float lvt_2_1_ = fastInvSqrt(lvt_1_1_);
            return new Quaternionf(quaternion.x()*lvt_2_1_,quaternion.y()*lvt_2_1_,
                    quaternion.z()*lvt_2_1_,quaternion.w()*lvt_2_1_);
        } else {
            return new Quaternionf(0.0F,0.0F,0.0F,0.0F);
        }
    }
    private static float fastInvSqrt(float p_226165_0_) {
        float lvt_1_1_ = 0.5F * p_226165_0_;
        int lvt_2_1_ = Float.floatToIntBits(p_226165_0_);
        lvt_2_1_ = 1597463007 - (lvt_2_1_ >> 1);
        p_226165_0_ = Float.intBitsToFloat(lvt_2_1_);
        p_226165_0_ *= 1.5F - lvt_1_1_ * p_226165_0_ * p_226165_0_;
        return p_226165_0_;
    }

    public static Quaternionf getQuaternionFromEntity(Entity entity){

        float pitch = entity.getXRot();
        float yaw = entity.getYRot();

        Quaternionf pitchQuat = new Quaternionf(new AxisAngle4f((float)(pitch * Math.PI/180), 1, 0, 0));

        Quaternionf yawQuat = new Quaternionf(new AxisAngle4f((float)(-yaw * Math.PI/180), 0 ,1, 0));

        yawQuat.mul(pitchQuat);
        return yawQuat;
    }

    public static Quaternionf slerp(Quaternionf start, Quaternionf end, float t) {
        float dot = start.x() * end.x() + start.y() * end.y() + start.z() * end.z() +
                start.w() * end.w();

        // 内積が負の場合、四元数の符号を反転して最短経路を選ぶ
        if (dot < 0.0f) {
            end = new Quaternionf(-end.x(), -end.y(), -end.z(), -end.w());
            dot = -dot;
        }

        float theta_0 = Math.acos(dot);
        float theta = theta_0 * t;
        float sinTheta = Math.sin(theta);
        float sinTheta_0 = Math.sin(theta_0);

        float s0 = Math.cos(theta) - dot * sinTheta / sinTheta_0;
        float s1 = sinTheta / sinTheta_0;

        return new Quaternionf(
                (s0 * start.x()) + (s1 * end.x),
                (s0 * start.y()) + (s1 * end.y),
                (s0 * start.z()) + (s1 * end.z),
                (s0 * start.w()) + (s1 * end.w)
        );
    }
}
