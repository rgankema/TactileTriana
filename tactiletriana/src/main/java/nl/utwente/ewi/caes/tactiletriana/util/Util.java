/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.utwente.ewi.caes.tactiletriana.util;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

/**
 *
 * @author Richard
 */
public class Util {
    public static long toEpochMinutes(LocalDateTime time) {
        return time.toEpochSecond(ZoneOffset.UTC) / 60;
    }
}
