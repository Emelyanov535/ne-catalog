import React from 'react';

const marketplaceLogos = {
    wildberries: 'https://foni.papik.pro/uploads/posts/2024-09/foni-papik-pro-ndr9-p-kartinki-vaildberriz-na-prozrachnom-fone-1.png',
    ozon: 'https://img.icons8.com/color/600/ozon.png',
};

type MarketplaceLogoProps = {
    marketplace: string;
    className?: string;
    width?: number;
    height?: number;
};

const MarketplaceLogo: React.FC<MarketplaceLogoProps> = ({
         marketplace,
         className = '',
         width = 45,
         height = 45,
     }) => {
    const normalizedMarketplace = marketplace.toLowerCase().trim();

    let logoPath;

    if (normalizedMarketplace.includes('wildberries') || normalizedMarketplace === 'wb') {
        logoPath = marketplaceLogos.wildberries;
    } else if (normalizedMarketplace.includes('ozon')) {
        logoPath = marketplaceLogos.ozon;
    } else {
        return <span className={className}>{marketplace}</span>;
    }

    return (
        <img
            src={logoPath}
            alt={marketplace}
            className={className}
            width={width}
            height={height}
            onError={(e) => {
                (e.target as HTMLImageElement).style.display = 'none';
            }}
        />
    );
};

export default MarketplaceLogo;