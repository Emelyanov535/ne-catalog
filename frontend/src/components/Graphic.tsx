import {ChartConfig, ChartContainer, ChartTooltip, ChartTooltipContent,} from "@/components/ui/chart.tsx"
import {CartesianGrid, Line, LineChart, XAxis} from "recharts"
import {useEffect, useState} from "react"
import {PriceStatsData} from "@/types/AnalysisDataDto.tsx";

const chartConfig = {
    min: {
        label: "Минимум",
        color: "var(--chart-1)",
    },
    max: {
        label: "Максимум",
        color: "var(--chart-2)",
    },
    avg: {
        label: "Средняя",
        color: "var(--chart-3)",
    },
} satisfies ChartConfig

export function Graphic({priceStats}: { priceStats: PriceStatsData[] }) {
    const [chartData, setChartData] = useState<any[]>([])

    const data = priceStats.map((item: any) => ({
        date: new Date(item.date).toLocaleDateString("ru-RU", {
            day: "2-digit",
            month: "short",
        }),
        min: item.minPrice ?? 0,
        max: item.maxPrice ?? 0,
        avg: item.avgPrice ?? 0,
    }))

    useEffect(() => {
        setChartData(data)
    }, []);

    return (
        <ChartContainer config={chartConfig}>
            <LineChart
                accessibilityLayer
                data={chartData}
                margin={{left: 12, right: 12}}
            >
                <CartesianGrid vertical={false}/>
                <XAxis
                    dataKey="date"
                    tickLine={false}
                    axisLine={false}
                    tickMargin={8}
                    tickFormatter={(value) => value.slice(0, 3)}
                />
                <ChartTooltip cursor={false} content={<ChartTooltipContent/>}/>
                <Line
                    dataKey="min"
                    type="monotone"
                    stroke="var(--color-min)"
                    strokeWidth={2}
                    dot={false}
                    connectNulls={true}
                />
                <Line
                    dataKey="max"
                    type="monotone"
                    stroke="var(--color-max)"
                    strokeWidth={2}
                    dot={false}
                    connectNulls={true}
                />
                <Line
                    dataKey="avg"
                    type="monotone"
                    stroke="var(--color-avg)"
                    strokeWidth={2}
                    dot={false}
                    connectNulls={true}
                />
            </LineChart>
        </ChartContainer>
    )
}
