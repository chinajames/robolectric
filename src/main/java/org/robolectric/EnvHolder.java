package org.robolectric;

import java.io.File;
import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.Map;

public class EnvHolder {
    public final Map<File, AndroidManifest> appManifestsByFile = new HashMap<File, AndroidManifest>();
    private final Map<SdkConfig, SoftReference<SdkEnvironment>> sdkToEnvironmentSoft = new HashMap<SdkConfig, SoftReference<SdkEnvironment>>();
    private SdkConfig lastSdkConfig;
    private SdkEnvironment lastSdkEnvironment;

    synchronized public SdkEnvironment getSdkEnvironment(SdkConfig sdkConfig, SdkEnvironment.Factory factory) {
        // keep the most recently-used SdkEnvironment strongly reachable to prevent thrashing in low-memory situations.
        if (sdkConfig.equals(lastSdkConfig)) {
            return lastSdkEnvironment;
        }

        SoftReference<SdkEnvironment> reference = sdkToEnvironmentSoft.get(sdkConfig);
        SdkEnvironment sdkEnvironment = reference == null ? null : reference.get();
        if (sdkEnvironment == null) {
            if (reference != null) {
                System.out.println("DEBUG: ********************* GC'ed SdkEnvironment reused!");
            }

            sdkEnvironment = factory.create();
            sdkToEnvironmentSoft.put(sdkConfig, new SoftReference<SdkEnvironment>(sdkEnvironment));
        }

        lastSdkConfig = sdkConfig;
        lastSdkEnvironment = sdkEnvironment;

        return sdkEnvironment;
    }
}
