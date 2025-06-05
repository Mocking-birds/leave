export interface LunarDate {
    year?: number;
    month?: number;
    day?: number;
    lunarYear: string;
    lunarMonth: string;
    lunarDay: string;
    solar: string;
}
export declare class Lunar {
    static lunar(year: number, month: number, day: number): LunarDate | null;
    /**
     * 获取节令
     * @param year 年
     * @param month 月
     */
    private static sectionalTerm;
    /**
     * 获取中气
     * @param year 年
     * @param month 月
     */
    private static principleTerm;
    /**
     * 节气修正
     * @param year 年
     * @param month 月
     * @param day 日
     */
    private static reviseSolarTerm;
    /**
     * 获取干支
     * @param cnYear 年
     */
    private static lunarYear;
    private static lunarMonth;
    private static lunarDay;
    /**
     * 获取农历下个月
     * @param year 年
     * @param month 月
     */
    private static nextCnMonth;
    /**
     * 获取农历月份天数
     * @param year 年
     * @param month 月
     */
    private static cnMonthDays;
}
