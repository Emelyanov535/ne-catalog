interface SearchResults {
    results: {
        marketplace: string,
        category: string,
        brand: string,
        productName: string,
        createdAt: string,
        url: string,
        imageUrl: string,
        percentChange: number
    }[],
    maxPage: number
}