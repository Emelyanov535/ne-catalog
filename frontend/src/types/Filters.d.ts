interface Filters {
    priceStart: number,
    priceEnd: number,
    filters: Record<string, Record<string, string[]>>
}