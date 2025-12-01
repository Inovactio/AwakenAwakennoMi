package com.inovactio.awakenawakennomi.api.abilities.components;

import com.inovactio.awakenawakennomi.init.ModAbilityKeys;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import xyz.pixelatedw.mineminenomi.api.abilities.IAbility;
import xyz.pixelatedw.mineminenomi.api.abilities.components.AbilityComponent;
import xyz.pixelatedw.mineminenomi.api.util.PriorityEventPool;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.stream.Stream;

public class BlockTriggerComponent extends AbilityComponent<IAbility> {

    private static final BiFunction<EventReduce, HitResult, EventReduce> ACCUMULATOR = (total, next) -> {
        if (next == HitResult.FAIL) {
            ++total.fails;
        } else if (next == HitResult.HIT) {
            ++total.hits;
        }
        return total;
    };

    private static final BinaryOperator<EventReduce> COMBINER = (a, b) -> {
        a.fails += b.fails;
        a.hits += b.hits;
        return a;
    };

    private final PriorityEventPool<ITryBlockHitEvent> tryBlockHitEvents = new PriorityEventPool<>();
    private final PriorityEventPool<IOnBlockHitEvent> onBlockHitEvents = new PriorityEventPool<>();
    private HitResult result = HitResult.PASS;

    public BlockTriggerComponent(IAbility ability) {
        super(ModAbilityKeys.BLOCK_TRIGGER, ability);
    }

    public BlockTriggerComponent addTryBlockHitEvent(int priority, ITryBlockHitEvent event) {
        this.tryBlockHitEvents.addEvent(priority, event);
        return this;
    }

    public BlockTriggerComponent addOnBlockHitEvent(int priority, IOnBlockHitEvent event) {
        this.onBlockHitEvents.addEvent(priority, event);
        return this;
    }

    public HitResult tryHit(LivingEntity entity, BlockPos pos, World world) {
        this.ensureIsRegistered();
        if (this.tryBlockHitEvents.getEventsStream().count() <= 0L) {
            this.result = HitResult.PASS;
        } else {
            Stream<HitResult> hitResults = this.tryBlockHitEvents.getEventsStream()
                    .map(event -> event.tryHit(entity, pos, world, this.getAbility()));
            EventReduce reduce = hitResults.reduce(new EventReduce(), ACCUMULATOR, COMBINER);
            if (reduce.fails > 0) {
                this.result = HitResult.FAIL;
            } else if (reduce.hits > 0) {
                this.result = HitResult.HIT;
            } else {
                this.result = HitResult.PASS;
            }
        }
        return this.result;
    }

    public boolean onHit(LivingEntity entity, BlockPos pos, World world) {
        this.ensureIsRegistered();
        boolean isCancelled = false;
        if (this.result == HitResult.HIT) {
            isCancelled = this.onBlockHitEvents.dispatchCancelable(
                    event -> !event.onHit(entity, pos, world, this.getAbility())
            );
        }
        this.result = HitResult.PASS;
        return !isCancelled;
    }

    public enum HitResult {
        HIT, PASS, FAIL;
    }

    static class EventReduce {
        public int fails;
        public int hits;
    }

    @FunctionalInterface
    public interface IOnBlockHitEvent {
        boolean onHit(LivingEntity entity, BlockPos pos, World world, IAbility ability);
    }

    @FunctionalInterface
    public interface ITryBlockHitEvent {
        HitResult tryHit(LivingEntity entity, BlockPos pos, World world, IAbility ability);
    }
}
