package com.peco2282.bcreborn.api.transport;

public record StripeHandlerEntry(
        IStripesHandler handler,
        int priority
) {
    public StripeHandlerEntry(IStripesHandler handler) {
        this(handler, 0);
    }

    public static StripeHandlerEntry of(IStripesHandler handler) {
        return new StripeHandlerEntry(handler);
    }

    public static StripeHandlerEntry of(IStripesHandler handler, int priority) {
        return new StripeHandlerEntry(handler, priority);
    }
}
