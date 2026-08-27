package org.crafterscr.craftersnpcanimations.entity;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;

public class AnimatedNpcEntity extends PathfinderMob {

    private static final EntityDataAccessor<String> NPC_ID =
            SynchedEntityData.defineId(
                    AnimatedNpcEntity.class,
                    EntityDataSerializers.STRING
            );

    private static final EntityDataAccessor<String> SKIN_URL =
            SynchedEntityData.defineId(
                    AnimatedNpcEntity.class,
                    EntityDataSerializers.STRING
            );

    private static final EntityDataAccessor<Boolean> SLIM_MODEL =
            SynchedEntityData.defineId(
                    AnimatedNpcEntity.class,
                    EntityDataSerializers.BOOLEAN
            );

    private static final EntityDataAccessor<String> ANIMATION =
            SynchedEntityData.defineId(
                    AnimatedNpcEntity.class,
                    EntityDataSerializers.STRING
            );

    private static final EntityDataAccessor<Boolean> ANIMATION_LOOP =
            SynchedEntityData.defineId(
                    AnimatedNpcEntity.class,
                    EntityDataSerializers.BOOLEAN
            );

    private static final EntityDataAccessor<Long> ANIMATION_START =
            SynchedEntityData.defineId(
                    AnimatedNpcEntity.class,
                    EntityDataSerializers.LONG
            );

    public AnimatedNpcEntity(
            EntityType<? extends PathfinderMob> entityType,
            Level level
    ) {
        super(entityType, level);

        setPersistenceRequired();
    }

    public static AttributeSupplier.Builder createAttributes() {

        return PathfinderMob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 20.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.25D)
                .add(Attributes.FOLLOW_RANGE, 32.0D);
    }

    @Override
    protected void defineSynchedData(
            SynchedEntityData.Builder builder
    ) {
        super.defineSynchedData(builder);

        builder.define(NPC_ID, "");
        builder.define(SKIN_URL, "");
        builder.define(SLIM_MODEL, false);

        builder.define(ANIMATION, "");
        builder.define(ANIMATION_LOOP, false);
        builder.define(ANIMATION_START, 0L);
    }

    /*
     * ============================
     * NPC ID
     * ============================
     */

    public String getNpcId() {
        return entityData.get(NPC_ID);
    }

    public void setNpcId(String id) {
        entityData.set(NPC_ID, id == null ? "" : id);
    }

    /*
     * ============================
     * SKIN
     * ============================
     */

    public String getSkinUrl() {
        return entityData.get(SKIN_URL);
    }

    public void setSkinUrl(String url) {
        entityData.set(
                SKIN_URL,
                url == null ? "" : url
        );
    }

    public boolean isSlimModel() {
        return entityData.get(SLIM_MODEL);
    }

    public void setSlimModel(boolean slim) {
        entityData.set(SLIM_MODEL, slim);
    }

    /*
     * ============================
     * ANIMACIÓN
     * ============================
     */

    public String getAnimationId() {
        return entityData.get(ANIMATION);
    }

    public boolean isAnimationLooping() {
        return entityData.get(ANIMATION_LOOP);
    }

    public long getAnimationStart() {
        return entityData.get(ANIMATION_START);
    }

    public boolean isAnimationPlaying() {
        return !getAnimationId().isBlank();
    }

    public void playAnimation(
            String animation,
            boolean loop
    ) {

        entityData.set(
                ANIMATION,
                animation == null ? "" : animation
        );

        entityData.set(
                ANIMATION_LOOP,
                loop
        );

        entityData.set(
                ANIMATION_START,
                level().getGameTime()
        );
    }

    public void stopAnimation() {

        entityData.set(
                ANIMATION,
                ""
        );

        entityData.set(
                ANIMATION_LOOP,
                false
        );

        entityData.set(
                ANIMATION_START,
                0L
        );
    }

    /*
     * ============================
     * GUARDADO
     * ============================
     */

    @Override
    public void addAdditionalSaveData(
            CompoundTag tag
    ) {

        super.addAdditionalSaveData(tag);

        tag.putString(
                "NpcId",
                getNpcId()
        );

        tag.putString(
                "SkinUrl",
                getSkinUrl()
        );

        tag.putBoolean(
                "SlimModel",
                isSlimModel()
        );

        tag.putString(
                "Animation",
                getAnimationId()
        );

        tag.putBoolean(
                "AnimationLoop",
                isAnimationLooping()
        );
    }

    @Override
    public void readAdditionalSaveData(
            CompoundTag tag
    ) {

        super.readAdditionalSaveData(tag);

        setNpcId(
                tag.getString("NpcId")
        );

        setSkinUrl(
                tag.getString("SkinUrl")
        );

        setSlimModel(
                tag.getBoolean("SlimModel")
        );

        String animation =
                tag.getString("Animation");

        boolean loop =
                tag.getBoolean("AnimationLoop");

        if (!animation.isBlank()) {
            playAnimation(
                    animation,
                    loop
            );
        }
    }
}