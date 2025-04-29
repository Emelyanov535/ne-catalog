import {useEffect, useState} from "react";
import {TrendingUp} from "lucide-react";
import {CartesianGrid, Line, LineChart, XAxis} from "recharts";
import {toast} from "sonner";

import {Card, CardContent, CardDescription, CardFooter, CardHeader, CardTitle} from "@/components/ui/card";
import {ChartConfig, ChartContainer, ChartTooltip, ChartTooltipContent} from "@/components/ui/chart";
import {forecastService} from "@/services/ForecastService";

export function Component(url: string) {
    // Состояние для исторических и прогнозируемых данных
    const [historicalData, setHistoricalData] = useState<any[]>([]);
    const [predictedData, setPredictedData] = useState<any[]>([]);

    // Функция для получения данных с бэка
    const fetchForecastData = async () => {
        try {
            const forecastResponse = await forecastService.getForecast(url);
            if (forecastResponse) {
                setHistoricalData(forecastResponse.historical);
                setPredictedData(forecastResponse.prediction);
            }
        } catch (error) {
            toast.error("Ошибка при получении прогноза: " + error);
        }
    };

    // Вызываем функцию получения данных при монтировании компонента
    useEffect(() => {
        fetchForecastData();
    }, [url]);

    // Объединяем исторические и прогнозируемые данные в один массив
    const chartData = [
        ...historicalData, // Исторические данные
        ...predictedData,  // Прогнозируемые данные
    ];



    const chartConfig = {
        value: {
            label: "Value",
            color: "hsl(var(--chart-1))"
        }
    } satisfies ChartConfig

    const formattedData = chartData.map((entry) => ({
        ...entry,
        date: new Date(entry.date).toLocaleDateString(), // Форматируем дату
    }));

    console.log(formattedData)

    return (
        <Card>
            <CardHeader>
                <CardTitle>Line Chart</CardTitle>
                <CardDescription>March 2025 Forecast</CardDescription>
            </CardHeader>
            <CardContent>
                <ChartContainer config={chartConfig}>
                    <LineChart
                        accessibilityLayer
                        data={formattedData} // Используем форматированные данные
                        margin={{
                            left: 12,
                            right: 12,
                        }}
                    >
                        <CartesianGrid vertical={false} />
                        <XAxis
                            dataKey="date" // Указываем, что ось X зависит от "date"
                            tickLine={false}
                            axisLine={false}
                            tickMargin={8}
                            tickFormatter={(value) => value} // Показываем отформатированную дату
                        />
                        <ChartTooltip
                            cursor={false}
                            content={<ChartTooltipContent hideLabel />}
                        />
                        <Line
                            dataKey="value"
                            type="natural"
                            stroke="#F22FE0"
                            strokeWidth={2}
                            dot={false}
                        />
                    </LineChart>
                </ChartContainer>
            </CardContent>
            <CardFooter className="flex-col items-start gap-2 text-sm">
                <div className="flex gap-2 font-medium leading-none">
                    Trending up by 5.2% this month <TrendingUp className="h-4 w-4"/>
                </div>
                <div className="leading-none text-muted-foreground">
                    Showing forecast for March 2025
                </div>
            </CardFooter>
        </Card>
    );
}
