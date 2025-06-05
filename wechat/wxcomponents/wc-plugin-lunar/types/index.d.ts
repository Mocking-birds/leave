import { type Plugin, type WcYear, type CalendarDay, type TrackDateResult, type TrackYearResult } from '@lspriv/wx-calendar/lib';
import { Lunar } from './lunar';
export declare class LunarPlugin implements Plugin {
    static KEY: "lunar";
    getLunar(date: CalendarDay): ReturnType<(typeof Lunar)['lunar']>;
    PLUGIN_TRACK_DATE(date: CalendarDay): TrackDateResult;
    PLUGIN_TRACK_YEAR(year: WcYear): TrackYearResult;
}
export declare const LUNAR_PLUGIN_KEY: "lunar";
