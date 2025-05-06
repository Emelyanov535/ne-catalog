export interface AnalysisDataDto {
    bestPurchaseTime: string;
    predictionAdvice: string;
    priceStats: PriceStatsData[];
}

export interface PriceStatsData {
    date: string;
    avgPrice: number;
    minPrice: number;
    maxPrice: number;
}