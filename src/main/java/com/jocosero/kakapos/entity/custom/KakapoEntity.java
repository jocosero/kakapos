package com.jocosero.kakapos.entity.custom;

import com.jocosero.kakapos.sound.ModSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.animal.ShoulderRidingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class KakapoEntity extends ShoulderRidingEntity {
    private static final Ingredient FOOD_ITEMS = Ingredient.of(Items.APPLE, Items.CARROT, Items.GOLDEN_APPLE);
    public final AnimationState idleAnimationState = new AnimationState();
    public final AnimationState danceAnimationState = new AnimationState();
    public final AnimationState sitAnimationState = new AnimationState();
    public final AnimationState preenAnimationState = new AnimationState();
    public final AnimationState fallAnimationState = new AnimationState();
    public final AnimationState runAnimationState = new AnimationState();

    private boolean partyParrot;
    @Nullable
    private BlockPos jukebox;
    private int idleAnimationTimeout = 0;
    private int fallAnimationTimeout = 0;
    private int preenAnimationTimeout = this.random.nextInt(11) + 10;
    private double lastTickPosX, lastTickPosY, lastTickPosZ;

    public KakapoEntity(EntityType<? extends ShoulderRidingEntity> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public static AttributeSupplier setAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 12)
                .add(Attributes.MOVEMENT_SPEED, 0.15F)
                .build();
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new PanicGoal(this, 1.8D));
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(0, new LookAtPlayerGoal(this, Player.class, 6.0F));
        this.goalSelector.addGoal(1, new SitWhenOrderedToGoal(this));
        this.goalSelector.addGoal(1, new FollowOwnerGoal(this, 1.7D, 3.0F, 1.0F, false));
        this.goalSelector.addGoal(2, new TemptGoal(this, 1.0D, FOOD_ITEMS, false));
        this.goalSelector.addGoal(3, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(4, new FollowMobGoal(this, 1.0D, 3.0F, 5.0F));
    }

    public void aiStep() {
        this.lastTickPosX = this.getX();
        this.lastTickPosY = this.getY();
        this.lastTickPosZ = this.getZ();
        if (this.jukebox == null || !this.jukebox.closerToCenterThan(this.position(), 3.46D) || !this.level().getBlockState(this.jukebox).is(Blocks.JUKEBOX)) {
            this.partyParrot = false;
            this.jukebox = null;
        }
        if (this.partyParrot) {
            this.danceAnimationState.startIfStopped(this.tickCount);
        } else {
            this.danceAnimationState.stop();
        }

        super.aiStep();
        Vec3 vec3 = this.getDeltaMovement();
        if (!this.onGround() && vec3.y < 0.0D) {
            this.setDeltaMovement(vec3.multiply(1.0D, 0.8D, 1.0D));
        }

        if (this.isPassenger() && !this.isOrderedToSit() && this.getVehicle() instanceof Player player) {
            this.setNoAi(false);
            if (shouldStopRiding(player)) {
                this.setPos(player.position());
                this.stopRiding();
            }
        }

        if (this.isInSittingPose()) {
            this.sitAnimationState.start(this.tickCount);
        } else {
            this.sitAnimationState.stop();
        }

        if (this.level().isClientSide) {
            setupAnimationStates();
//            setupPreenAnimationState();
            setupFallAnimationState();
        }
    }

    private void setupAnimationStates() {
        if (this.idleAnimationTimeout <= 0) {
            this.idleAnimationTimeout = this.random.nextInt(40) + 80;
            this.idleAnimationState.startIfStopped(this.tickCount);
        } else {
            --this.idleAnimationTimeout;
        }
    }

    private void setupFallAnimationState() {
        if (!this.onGround()) {
            ++this.fallAnimationTimeout;
            if (this.fallAnimationTimeout >= 10) {
                this.fallAnimationState.startIfStopped(this.tickCount);
            }
        } else {
            this.fallAnimationTimeout = 0;
            this.fallAnimationState.stop();
        }
    }

    @Override
    protected void updateWalkAnimation(float pPartialTick) {
        float f;
        if (this.getPose() == Pose.STANDING && isMoving()) {
            f = Math.min(pPartialTick + 6F, 1F);
        } else {
            f = 0F;
        }

        this.walkAnimation.update(f, 0.2F);
    }

    private boolean isMoving() {
        double dx = this.getX() - this.lastTickPosX;
        double dy = this.getY() - this.lastTickPosY;
        double dz = this.getZ() - this.lastTickPosZ;
        return dx * dx + dy * dy + dz * dz > 0.001D;
    }

//    private void setupPreenAnimationState() {
//        if (this.preenAnimationTimeout <= 0) {
//            this.preenAnimationTimeout = this.random.nextInt(11) + 10;
//            this.preenAnimationState.startIfStopped(this.tickCount);
//        } else {
//            --this.preenAnimationTimeout;
//        }
//    }

    public InteractionResult mobInteract(Player pPlayer, InteractionHand pHand) {
        ItemStack itemstack = pPlayer.getItemInHand(pHand);
        if (!this.isTame() && FOOD_ITEMS.test(itemstack)) {
            if (!pPlayer.getAbilities().instabuild) {
                itemstack.shrink(1);
            }

            if (!this.isSilent()) {
                this.level().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.PARROT_EAT, this.getSoundSource(), 1.0F, 1.0F + (this.random.nextFloat() - this.random.nextFloat()) * 0.2F);
            }

            if (!this.level().isClientSide) {
                if (this.random.nextInt(10) == 0 && !net.minecraftforge.event.ForgeEventFactory.onAnimalTame(this, pPlayer)) {
                    this.tame(pPlayer);
                    this.level().broadcastEntityEvent(this, (byte) 7);
                } else {
                    this.level().broadcastEntityEvent(this, (byte) 6);
                }
            }

            return InteractionResult.sidedSuccess(this.level().isClientSide);
        } else if (this.isTame() && this.isOwnedBy(pPlayer)) {
            if (this.isFood(itemstack) && this.getHealth() < this.getMaxHealth()) {
                if (!pPlayer.getAbilities().instabuild) {
                    itemstack.shrink(1);
                }

                this.heal((float) itemstack.getFoodProperties(this).getNutrition());
                this.gameEvent(GameEvent.EAT, this);
                return InteractionResult.SUCCESS;
            }
            if (!this.level().isClientSide) {
                this.setOrderedToSit(!this.isOrderedToSit());
                return super.mobInteract(pPlayer, pHand);
            }

            return InteractionResult.sidedSuccess(true);
        } else {
            return super.mobInteract(pPlayer, pHand);
        }
    }

    @Override
    public void setPos(double p_20210_, double p_20211_, double p_20212_) {
        if (this.isPassenger()) {
            if (this.isFallFlying()) {
                p_20211_ += 1;
            }
            p_20211_ += 0.38;
        }
        super.setPos(p_20210_, p_20211_, p_20212_);
    }

    public boolean isFood(ItemStack pStack) {
        return FOOD_ITEMS.test(pStack);
    }

    public void setRecordPlayingNearby(BlockPos pPos, boolean pIsPartying) {
        this.jukebox = pPos;
        partyParrot = pIsPartying;
    }

    public boolean isPartyParrot() {
        return this.partyParrot;
    }

    public boolean isPushable() {
        return true;
    }

    protected void doPush(Entity pEntity) {
        if (!(pEntity instanceof Player)) {
            super.doPush(pEntity);
        }
    }

    public boolean hurt(DamageSource pSource, float pAmount) {
        if (this.isMoving()) {
            this.runAnimationState.start(this.tickCount);
        } else {
            this.runAnimationState.stop();
        }
        if (this.isInvulnerableTo(pSource)) {
            return false;
        } else {
            if (!this.level().isClientSide) {
                this.setOrderedToSit(false);
            }

            return super.hurt(pSource, pAmount);
        }
    }

    private boolean shouldStopRiding(Player player) {
        return player.isCrouching() || player.isInFluidType() || player.isSpectator() || player.isSleeping();
    }

    public boolean causeFallDamage(float pFallDistance, float pMultiplier, DamageSource pSource) {
        return false;
    }

    protected SoundEvent getAmbientSound() {
        return ModSounds.KAKAPO_AMBIENT.get();
    }

    protected SoundEvent getHurtSound(DamageSource pDamageSource) {
        return ModSounds.KAKAPO_HURT.get();
    }

    protected SoundEvent getDeathSound() {
        return ModSounds.KAKAPO_DEATH.get();
    }

    protected void playStepSound(BlockPos pPos, BlockState pBlock) {
        this.playSound(SoundEvents.CHICKEN_STEP, 0.15F, 0.8F);
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel pLevel, AgeableMob pOtherParent) {
        return null;
    }

}