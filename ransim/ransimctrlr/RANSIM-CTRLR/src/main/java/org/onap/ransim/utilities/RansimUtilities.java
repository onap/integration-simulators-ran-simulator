/*-
 * ============LICENSE_START=======================================================
 * Ran Simulator Controller
 * ================================================================================
 * Copyright (C) 2020 Wipro Limited.
 * ================================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ============LICENSE_END=========================================================
 */

package org.onap.ransim.utilities;

/**
 *
 * Class with Utilities used across ransim
 */
public class RansimUtilities {

    /**
     * @param angle
     * @return
     */
    public static double degToRadians(double angle) {
        double radians = 57.2957795;
        return (angle / radians);
    }

    /**
     * @param angle
     * @return
     */
    public static double metersDeglon(double angle) {
        double d2r = degToRadians(angle);
        return ((111415.13 * Math.cos(d2r)) - (94.55 * Math.cos(3.0 * d2r)) + (0.12 * Math.cos(5.0 * d2r)));
    }

    /**
     * @param angle
     * @return
     */
    public static double metersDeglat(double angle) {
        double d2r = degToRadians(angle);
        return (111132.09 - (566.05 * Math.cos(2.0 * d2r)) + (1.20 * Math.cos(4.0 * d2r))
                - (0.002 * Math.cos(6.0 * d2r)));
    }
}
