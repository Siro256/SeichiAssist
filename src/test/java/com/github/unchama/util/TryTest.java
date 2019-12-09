package com.github.unchama.util;

import com.github.unchama.util.failable.FailableAction;
import com.github.unchama.util.failable.Try;
import com.github.unchama.util.unit.Unit;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;

import java.util.Optional;

/**
 * @author Monchi
 */
public class TryTest {
    @Test
    public void testSuccessfulTry() {
        Try<Unit> result = Try.sequence(
                new FailableAction<>(
                        Unit.instance,
                        this::ok
                )
        );

        Assertions.assertEquals(ActionStatus.Ok, result.overallStatus());
        Assertions.assertEquals(Optional.empty(), result.failedValue());
    }

    @Test
    public void testFailedTry() {
        Try<Unit> result = Try.sequence(
                new FailableAction<>(
                        Unit.instance,
                        this::fail
                )
        );

        Assertions.assertEquals(ActionStatus.Fail, result.overallStatus());
        Assertions.assertEquals(Optional.of(Unit.instance), result.failedValue());
    }
    
    private ActionStatus ok() {
        return ActionStatus.Ok;
    }

    private ActionStatus fail() {
        return ActionStatus.Fail;
    }
}
