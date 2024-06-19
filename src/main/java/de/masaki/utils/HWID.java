package de.masaki.utils;

import de.masaki.hwid.HwidKit;
import de.masaki.hwid.SystemSpecification;

public class HWID {
    public static String getHWID() {
        HwidKit hwidKit = new HwidKit();
        String generatedHwid = hwidKit.generateIdentifier(
                SystemSpecification.OS_NAME,
                SystemSpecification.OS_ARCHITECTURE,
                SystemSpecification.OS_VERSION,
                SystemSpecification.AVAILABLE_PROCESSORS
                );


        return generatedHwid;
    }
}
