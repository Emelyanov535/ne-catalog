import * as React from "react"
import {CartesianGrid, Line, LineChart, XAxis} from "recharts"

import {Card, CardContent, CardDescription, CardHeader, CardTitle,} from "@/components/ui/card"
import {ChartConfig, ChartContainer, ChartTooltip, ChartTooltipContent,} from "@/components/ui/chart"

const metricColors = {
    min: "var(--chart-1)",
    max: "var(--chart-2)",
    avg: "var(--chart-3)",
}

const chartConfig = {
    wildberries: {
        label: "Wildberries",
        color: "var(--chart-1)",
    },
    ozon: {
        label: "Ozon",
        color: "var(--chart-2)",
    },
    general: {
        label: "General",
        color: "var(--chart-3)",
    },
} satisfies ChartConfig

export function TestComponent({chartData}: { chartData: any }) {
    const normalizedChartData = React.useMemo(() => {
        if (!Array.isArray(chartData)) return [];

        return chartData.map((entry: any) => ({
            ...entry,
            wildberries: {
                min: entry.wildberries?.min ?? 0,
                max: entry.wildberries?.max ?? 0,
                avg: entry.wildberries?.avg ?? 0,
            },
            ozon: {
                min: entry.ozon?.min ?? 0,
                max: entry.ozon?.max ?? 0,
                avg: entry.ozon?.avg ?? 0,
            },
            general: {
                min: entry.general?.min ?? 0,
                max: entry.general?.max ?? 0,
                avg: entry.general?.avg ?? 0,
            },
        }));
    }, [chartData]);

    const [activeChart, setActiveChart] = React.useState<keyof typeof chartConfig>("wildberries")
    return (
        <Card>
            <CardHeader className="flex flex-col items-stretch space-y-0 border-b p-0 sm:flex-row">
                <div className="flex flex-1 flex-col justify-center gap-1 px-6 py-5 sm:py-6">
                    <CardTitle>Историческая динамика цен</CardTitle>
                    <CardDescription>
                        Минимальная, максимальная и средняя цена на товар по дням.
                    </CardDescription>
                </div>
                <div className="flex">
                    {["wildberries", "ozon", "general"].map((key) => {
                        const chart = key as keyof typeof chartConfig
                        return (
                            <button
                                key={chart}
                                data-active={activeChart === chart}
                                className="flex flex-1 flex-col justify-center gap-1 border-t px-6 py-4 text-left even:border-l data-[active=true]:bg-muted/50 sm:border-l sm:border-t-0 sm:px-8 sm:py-6"
                                onClick={() => setActiveChart(chart)}
                            >
                <span className="text-xs text-muted-foreground">
                  {chartConfig[chart].label}
                </span>
                            </button>
                        )
                    })}
                </div>
            </CardHeader>
            <CardContent className="px-2 sm:p-6">
                <ChartContainer
                    config={chartConfig}
                    className="aspect-auto h-[250px] w-full"
                >
                    <LineChart
                        accessibilityLayer
                        data={normalizedChartData}
                        margin={{
                            left: 12,
                            right: 12,
                        }}
                    >
                        <CartesianGrid vertical={false}/>
                        <XAxis
                            dataKey="date"
                            tickLine={false}
                            axisLine={false}
                            tickMargin={8}
                            tickFormatter={(value) => {
                                const date = new Date(value)
                                return date.toLocaleDateString("en-US", {
                                    month: "short",
                                    day: "numeric",
                                })
                            }}
                        />
                        <ChartTooltip
                            content={
                                <ChartTooltipContent
                                    className="w-[150px]"
                                    nameKey="wb"
                                    labelFormatter={(value) => {
                                        return new Date(value).toLocaleDateString("en-US", {
                                            month: "short",
                                            day: "numeric",
                                            year: "numeric",
                                        })
                                    }}
                                />
                            }
                        />
                        {["min", "max", "avg"].map((metric) => (
                            <Line
                                key={`${activeChart}-${metric}`}
                                type="monotone"
                                dataKey={`${activeChart}.${metric}`}
                                stroke={metricColors[metric as keyof typeof metricColors]}
                                strokeWidth={2}
                                dot={false}
                                name={metric}
                            />
                        ))}
                    </LineChart>

                </ChartContainer>
            </CardContent>
        </Card>
    )
}

export default TestComponent;