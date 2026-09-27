package com.github.tacowasa059.sphericalplayermod.common.Interface;


import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;

public interface ICustomPlayerData {
    void sphericalPlayerMod$setSize(float size);
    float sphericalPlayerMod$getSize();

    void sphericalPlayerMod$setRestitutionCoefficient(float value);
    float sphericalPlayerMod$getRestitutionCoefficient();
    void sphericalPlayerMod$setFlag(boolean flag);
    void sphericalPlayerMod$setFlagAndSizeAndRestitution(boolean flag, float size, float value);
    boolean sphericalPlayerMod$getFlag();
    void sphericalPlayerMod$setQuaternion(Quaternionf quaternion);

    Quaternionf sphericalPlayerMod$getQuaternion();

    Quaternionf sphericalPlayerMod$getInterpolatedQuaternion(float partialTicks);

    void sphericalPlayerMod$setCurrentPosition(Vec3 pos);
    Vec3 sphericalPlayerMod$getCurrentPosition();

    void sphericalPlayerMod$collideWith(Entity entity);
}
