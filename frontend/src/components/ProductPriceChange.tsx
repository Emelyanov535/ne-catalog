import {
    AlertDialog,
    AlertDialogAction,
    AlertDialogContent,
    AlertDialogDescription,
    AlertDialogFooter,
    AlertDialogHeader,
    AlertDialogTitle,
    AlertDialogTrigger,
} from "@/components/ui/alert-dialog"
import {Button} from "@/components/ui/button"
import {
    CalendarDaysIcon,
    ChevronDownIcon,
    ChevronRightIcon,
    ChevronUpIcon,
    InfoIcon,
    LightbulbIcon
} from "lucide-react";
import React from "react";
import {Graphic} from "@/components/Graphic.tsx";
import {AnalysisDataDto} from "@/types/AnalysisDataDto.tsx";
import {Tooltip, TooltipContent, TooltipTrigger} from "@/components/ui/tooltip.tsx";

export const ProductPriceChange: React.FC<{
    product: ProductDto,
    analysisData?: AnalysisDataDto | null
}> = ({
          product,
          analysisData
      }) => {
    return (
        <AlertDialog>
            <AlertDialogTrigger asChild>
                <Button
                    className="w-full text-sm font-semibold py-2 rounded-md flex justify-between items-center"
                >
                    <div
                        className={`flex items-center gap-1 text-xs ${
                            product.percentChange > 0 ? "text-red-600" : "text-green-600"
                        }`}
                    >
                        {product.percentChange > 0 ? (
                            <ChevronUpIcon className="w-4 h-4"/>
                        ) : (
                            <ChevronDownIcon className="w-4 h-4"/>
                        )}
                        <span>
                            {product.percentChange > 0
                                ? `+${product.percentChange}`
                                : `${Math.abs(product.percentChange)}`} ₽
                        </span>
                    </div>

                    <ChevronRightIcon className="size-4 text-muted-foreground"/>
                </Button>
            </AlertDialogTrigger>

            <AlertDialogContent className="max-w-2xl">
                <AlertDialogHeader>
                    <AlertDialogTitle>Детальный анализ цены</AlertDialogTitle>
                    <Graphic priceStats={analysisData?.priceStats ?? []} />
                    <AlertDialogDescription className="space-y-2">
                        {analysisData && (
                            <span className="block grid gap-3">
                                {analysisData.bestPurchaseTime && (
                                    <span className="flex items-center gap-2">
                                        <CalendarDaysIcon className="h-4 w-4 text-blue-500" />
                                        <span className="text-sm">{analysisData.bestPurchaseTime}</span>
                                    </span>
                                )}
                                {analysisData.predictionAdvice && (
                                    <span className="flex items-start gap-2">
                                        <LightbulbIcon className="h-4 w-4 text-yellow-500 mt-0.5" />
                                        <span className="flex-1">
                                            <span className="flex items-center gap-1">
                                                <span className="text-sm">{analysisData.predictionAdvice}</span>
                                                <Tooltip>
                                                    <TooltipTrigger asChild>
                                                        <InfoIcon className="h-3 w-3 text-gray-400 hover:text-gray-600 cursor-help" />
                                                    </TooltipTrigger>
                                                    <TooltipContent side="right" className="max-w-[300px]">
                                                        <span className="text-xs">
                                                            Данный совет основан на анализе исторических цен и рыночных тенденций.
                                                            Рекомендуем учитывать текущую экономическую ситуацию.
                                                        </span>
                                                    </TooltipContent>
                                                </Tooltip>
                                            </span>
                                        </span>
                                    </span>
                                )}
                            </span>
                        )}
                    </AlertDialogDescription>
                </AlertDialogHeader>

                <AlertDialogFooter>
                    <AlertDialogAction autoFocus>Понятно</AlertDialogAction>
                </AlertDialogFooter>
            </AlertDialogContent>
        </AlertDialog>
    );
}
