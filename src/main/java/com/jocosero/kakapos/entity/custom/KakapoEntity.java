package com.jocosero.kakapos.entity.custom;

import com.jocosero.kakapos.sound.ModSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
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
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;
import software.bernie.geckolib3.util.GeckoLibUtil;

import static software.bernie.geckolib3.core.builder.ILoopType.EDefaultLoopTypes.LOOP;

public class KakapoEntity extends ShoulderRidingEntity implements IAnimatable {
    private static final Ingredient FOOD_ITEMS = Ingredient.of(Items.APPLE, Items.GOLDEN_APPLE);
    public AnimationFactory factory = GeckoLibUtil.createFactory(this);
    private boolean partyParrot;
    @Nullable
    private BlockPos jukebox;

    public KakapoEntity(EntityType<? extends ShoulderRidingEntity> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public static AttributeSupplier setAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 12.0D)
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
        if (this.jukebox == null || !this.jukebox.closerToCenterThan(this.position(), 3.46D) || !this.level.getBlockState(this.jukebox).is(Blocks.JUKEBOX)) {
            this.partyParrot = false;
            this.jukebox = null;
        }
        super.aiStep();
        Vec3 vec3 = this.getDeltaMovement();
        if (!this.onGround && vec3.y < 0.0D) {
            this.setDeltaMovement(vec3.multiply(1.0D, 0.8D, 1.0D));
        }

        if (this.isPassenger() && !this.isOrderedToSit() && this.getVehicle() instanceof Player player) {
            this.setNoAi(false);
            if (shouldStopRiding(player)) {
                this.setPos(player.position());
                this.stopRiding();
            }
        }
    }


    public InteractionResult mobInteract(Player pPlayer, InteractionHand pHand) {
        ItemStack itemstack = pPlayer.getItemInHand(pHand);
        if (!this.isTame() && FOOD_ITEMS.test(itemstack)) {
            if (!pPlayer.getAbilities().instabuild) {
                itemstack.shrink(1);
            }

            if (!this.isSilent()) {
                this.level.playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.PARROT_EAT, this.getSoundSource(), 1.0F, 1.0F + (this.random.nextFloat() - this.random.nextFloat()) * 0.2F);
            }

            if (!this.level.isClientSide) {
                if (this.random.nextInt(10) == 0 && !net.minecraftforge.event.ForgeEventFactory.onAnimalTame(this, pPlayer)) {
                    this.tame(pPlayer);
                    this.level.broadcastEntityEvent(this, (byte) 7);
                } else {
                    this.level.broadcastEntityEvent(this, (byte) 6);
                }
            }

            return InteractionResult.sidedSuccess(this.level.isClientSide);
        } else if (this.isTame() && this.isOwnedBy(pPlayer)) {
            if (this.isFood(itemstack) && this.getHealth() < this.getMaxHealth()) {
                if (!pPlayer.getAbilities().instabuild) {
                    itemstack.shrink(1);
                }

                this.heal((float) itemstack.getFoodProperties(this).getNutrition());
                this.gameEvent(GameEvent.EAT, this);
                return InteractionResult.SUCCESS;
            }
            if (!this.level.isClientSide) {
                this.setOrderedToSit(!this.isOrderedToSit());
                return super.mobInteract(pPlayer, pHand);
            }

            if (itemstack.getItem() == Items.STICK) {
                this.startRiding(pPlayer, true);
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
        this.partyParrot = pIsPartying;
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
        if (this.isInvulnerableTo(pSource)) {
            return false;
        } else {
            if (!this.level.isClientSide) {
                this.setOrderedToSit(false);
            }

            return super.hurt(pSource, pAmount);
        }
    }

    //This one is Kakapo exclusive. It checks if the player is doing any action that wouldn't allow the kakapo to ride it.
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

    private <E extends IAnimatable> PlayState predicate(AnimationEvent<E> event) {

        if (isPartyParrot()) {
            event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.kakapo.dance", LOOP));
            return PlayState.CONTINUE;
        }

        if (!isOnGround()) {
            event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.kakapo.flapping", LOOP));
            return PlayState.CONTINUE;
        }

        if (event.isMoving()) {
            Vec3 vec3 = getDeltaMovement();
            double speed = Math.sqrt(vec3.x * vec3.x + vec3.z * vec3.z);
            if (speed > 0.06F) {
                event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.kakapo.running", LOOP));
                return PlayState.CONTINUE;
            }
            event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.kakapo.walking", LOOP));
        } else {
            if (!isInSittingPose()) {
                event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.kakapo.idle", LOOP));
            }
            if (isInSittingPose()) {
                event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.kakapo.sitting", LOOP));
            }
            return PlayState.CONTINUE;
        }

        return PlayState.CONTINUE;
    }

    @Override
    public void registerControllers(AnimationData data) {
        data.addAnimationController(new AnimationController(this, "controller",
                2, this::predicate));

    }

    @Override
    public AnimationFactory getFactory() {
        return factory;
    }
}