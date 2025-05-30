package ru.necatalog.ozon.parser.parsing.dto.catalog;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.Getter;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProductsInfo {

    @JsonAlias("items")
    private List<Info> infoBlocks;

    public Collection<OzonCatalogPageProductData> getProductsData() {
        return infoBlocks.stream()
            .map(infoBlock ->
                OzonCatalogPageProductData.builder()
                    .brand(getInfoBlockValue(infoBlock, AutomatiationId.BRAND))
                    .productName(getInfoBlockTextAtomValue(infoBlock, AutomatiationId.PRODUCT_NAME))
                    .url(infoBlock.action.link)
                    .imageUrl(getImageUrl(infoBlock))
                    .price(getPriceValue(infoBlock))
                    .build())
            .collect(Collectors.toSet());
    }

    private String getInfoBlockValue(Info infoBlock,
                                     AutomatiationId automatiationId) {
        return switch (automatiationId) {
            case BRAND -> infoBlock.mainState.stream()
                .filter(ms -> ms.labelList != null && ms.labelList.items != null)
                .flatMap(ms -> ms.labelList.items.stream())
                .filter(Objects::nonNull)
                .filter(item -> item.testInfo != null
                    && Objects.equals(item.testInfo.automatizationId, automatiationId.value))
                .map(item -> item.title)
                .findFirst()
                .orElse("");
            default -> "";
        };
    }

    private String getInfoBlockTextAtomValue(Info infoBlock,
                                             AutomatiationId automatiationId) {
        return switch (automatiationId) {
            case PRODUCT_NAME -> infoBlock.mainState.stream()
                .filter(ms -> ms.textAtom != null && ms.textAtom.testInfo != null)
                .map(ms -> ms.textAtom)
                .filter(ta -> automatiationId.getValue().equals(ta.testInfo.automatizationId))
                .map(ta -> ta.text)
                .findFirst()
                .orElse("");
            default -> "";
        };
    }

    private String getImageUrl(Info infoBlock) {
        return infoBlock.tileImage != null
            && infoBlock.tileImage.items != null
            && !infoBlock.tileImage.items.isEmpty()
            && infoBlock.tileImage.items.getFirst() != null
            && infoBlock.tileImage.items.getFirst().image != null
            && infoBlock.tileImage.items.getFirst().image.link != null
            ? infoBlock.tileImage.items.getFirst().image.link
            : "";
    }

    private String getPriceValue(Info infoBlock) {
        return infoBlock.mainState.stream()
            .filter(ms -> ms.price != null
                && ms.price.price != null
                && !ms.price.price.isEmpty())
            .flatMap(ms -> ms.price.price.stream())
            .filter(price -> "PRICE".equals(price.textStyle))
            .map(price -> price.text)
            .findFirst()
            .orElse("");
    }

    @Getter
    public enum AutomatiationId {
        LEFT_COUNT("tile-list-black-friday-stockbar"),
        ORIGINAL("tile-list-original-in-label"),
        PRODUCT_NAME("tile-name"),
        RATING("tile-list-rating"),
        COMMENTS_COUNT("tile-list-comments"),
        BRAND("tile-list-paid-brand")
        ;

        private final String value;

        AutomatiationId(String s) {
            this.value = s;
        }

    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Info {

        private LinkHolder action;

        private List<Attribute> mainState;

        private TileImage tileImage;

        @Data
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class LinkHolder {

            private String link;

        }

        @Data
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class Attribute {

            private String type;

            @JsonAlias("priceV2")
            private Price price;

            private LabelList labelList;

            private TextAtom textAtom;

            @Data
            @JsonIgnoreProperties(ignoreUnknown = true)
            public static class Price {

                private List<Item> price;

                private String discount;

                @Data
                @JsonIgnoreProperties(ignoreUnknown = true)
                public static class Item {

                    private String text;

                    private String textStyle;

                }

            }

            @Data
            @JsonIgnoreProperties(ignoreUnknown = true)
            public static class LabelList {

                List<LabelListItem> items;

                @Data
                @JsonIgnoreProperties(ignoreUnknown = true)
                public static class LabelListItem {

                    private String title;

                    private TestInfo testInfo;

                }

            }

            @Data
            @JsonIgnoreProperties(ignoreUnknown = true)
            public static class TextAtom {

                private String text;

                private TestInfo testInfo;

            }

            @Data
            @JsonIgnoreProperties(ignoreUnknown = true)
            public static class TestInfo {

                private String automatizationId;

            }

        }

    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class TileImage {

        private List<TileImageItem> items;

        @Data
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class TileImageItem {

            private Image image;

            @Data
            @JsonIgnoreProperties(ignoreUnknown = true)
            public static class Image {

                private String link;

            }

        }

    }

}