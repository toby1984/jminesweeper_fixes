// Generated by jextract

package com.voipfuture.jminesweep.client.nativelinux;

import java.lang.foreign.FunctionDescriptor;
import java.lang.invoke.MethodHandle;
final class constants$1 {

    // Suppresses default constructor, ensuring non-instantiability.
    private constants$1() {}
    static final FunctionDescriptor tcsetattr$FUNC = FunctionDescriptor.of(Constants$root.C_INT$LAYOUT,
        Constants$root.C_INT$LAYOUT,
        Constants$root.C_INT$LAYOUT,
        Constants$root.C_POINTER$LAYOUT
    );
    static final MethodHandle tcsetattr$MH = RuntimeHelper.downcallHandle(
        "tcsetattr",
        constants$1.tcsetattr$FUNC
    );
    static final FunctionDescriptor cfmakeraw$FUNC = FunctionDescriptor.ofVoid(
        Constants$root.C_POINTER$LAYOUT
    );
    static final MethodHandle cfmakeraw$MH = RuntimeHelper.downcallHandle(
        "cfmakeraw",
        constants$1.cfmakeraw$FUNC
    );
    static final FunctionDescriptor tcsendbreak$FUNC = FunctionDescriptor.of(Constants$root.C_INT$LAYOUT,
        Constants$root.C_INT$LAYOUT,
        Constants$root.C_INT$LAYOUT
    );
    static final MethodHandle tcsendbreak$MH = RuntimeHelper.downcallHandle(
        "tcsendbreak",
        constants$1.tcsendbreak$FUNC
    );
    static final FunctionDescriptor tcdrain$FUNC = FunctionDescriptor.of(Constants$root.C_INT$LAYOUT,
        Constants$root.C_INT$LAYOUT
    );
    static final MethodHandle tcdrain$MH = RuntimeHelper.downcallHandle(
        "tcdrain",
        constants$1.tcdrain$FUNC
    );
    static final FunctionDescriptor tcflush$FUNC = FunctionDescriptor.of(Constants$root.C_INT$LAYOUT,
        Constants$root.C_INT$LAYOUT,
        Constants$root.C_INT$LAYOUT
    );
    static final MethodHandle tcflush$MH = RuntimeHelper.downcallHandle(
        "tcflush",
        constants$1.tcflush$FUNC
    );
    static final FunctionDescriptor tcflow$FUNC = FunctionDescriptor.of(Constants$root.C_INT$LAYOUT,
        Constants$root.C_INT$LAYOUT,
        Constants$root.C_INT$LAYOUT
    );
    static final MethodHandle tcflow$MH = RuntimeHelper.downcallHandle(
        "tcflow",
        constants$1.tcflow$FUNC
    );
}


