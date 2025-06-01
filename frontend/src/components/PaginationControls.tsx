import React from "react";
import {
    Pagination,
    PaginationContent,
    PaginationEllipsis,
    PaginationItem,
    PaginationLink,
    PaginationNext,
    PaginationPrevious
} from "@/components/ui/pagination";

interface PaginationControlsProps {
    currentPage: number;
    maxPage: number;
    onPageChange: (newPage: number) => void;
}

export const PaginationControls: React.FC<PaginationControlsProps> = ({
                                                                          currentPage,
                                                                          maxPage,
                                                                          onPageChange
                                                                      }) => {
    const getPaginationRange = () => {
        const range = [];
        let start = Math.max(currentPage - 2, 1);
        let end = Math.min(currentPage + 2, maxPage);

        if (start === 1) {
            end = Math.min(5, maxPage);
        }

        if (end === maxPage) {
            start = Math.max(maxPage - 4, 1);
        }

        for (let i = start; i <= end; i++) {
            range.push(i);
        }

        return range;
    };

    return (
        <Pagination className="p-10">
            <PaginationContent>
                <PaginationItem>
                    <PaginationPrevious
                        href="#"
                        onClick={() => onPageChange(Math.max(currentPage - 1, 1))}
                    />
                </PaginationItem>

                {getPaginationRange().map((pageNum) => (
                    <PaginationItem key={pageNum}>
                        <PaginationLink
                            href="#"
                            isActive={currentPage === pageNum}
                            onClick={() => onPageChange(pageNum)}
                        >
                            {pageNum}
                        </PaginationLink>
                    </PaginationItem>
                ))}

                {maxPage > 5 && currentPage < maxPage - 2 && (
                    <PaginationItem><PaginationEllipsis /></PaginationItem>
                )}

                <PaginationItem>
                    <PaginationNext
                        href="#"
                        onClick={() => onPageChange(Math.min(currentPage + 1, maxPage))}
                    />
                </PaginationItem>
            </PaginationContent>
        </Pagination>
    );
};
