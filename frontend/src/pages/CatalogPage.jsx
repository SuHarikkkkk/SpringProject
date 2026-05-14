import { useEffect, useState } from "react";
import { Search } from "lucide-react";
import { PageTitle } from "../components/layout/PageTitle.jsx";
import { Card } from "../components/ui/Card.jsx";
import { SelectField } from "../components/ui/SelectField.jsx";
import { ProductRow } from "../components/product/ProductRow.jsx";
import { getProducts } from "../services/productService.js";

export function CatalogPage() {
    const [products, setProducts] = useState([]);
    const [pageData, setPageData] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState("");
    const [page, setPage] = useState(0);

    useEffect(() => {
        setLoading(true);
        setError("");

        getProducts(page, 10)
            .then((data) => {
                setProducts(data.content || []);
                setPageData(data);
            })
            .catch((err) => {
                console.error(err);
                setError("Не удалось загрузить товары");
            })
            .finally(() => {
                setLoading(false);
            });
    }, [page]);

    function goToPrevPage() {
        if (page > 0) {
            setPage(page - 1);
        }
    }

    function goToNextPage() {
        if (pageData && !pageData.last) {
            setPage(page + 1);
        }
    }

    return (
        <div>
            <PageTitle
                title="Product Catalog"
                subtitle="Browse categories, search cosmetics, and add items to cart."
            />

            <div className="mb-6 grid gap-4 md:grid-cols-4">
                <Card className="md:col-span-2">
                    <div className="flex items-center gap-3 rounded-2xl border px-4 py-3">
                        <Search className="h-4 w-4 text-slate-500" />
                        <input className="w-full outline-none" placeholder="Search products..." />
                    </div>
                </Card>

                <Card>
                    <SelectField
                        label="Category"
                        options={["All", "Skincare", "Makeup", "Cleansing"]}
                    />
                </Card>

                <Card>
                    <SelectField
                        label="Sort by"
                        options={["Newest", "Price: low to high", "Price: high to low"]}
                    />
                </Card>
            </div>

            {loading && <p>Загрузка товаров...</p>}

            {!loading && error && (
                <p className="text-red-600">{error}</p>
            )}

            {!loading && !error && (
                <>
                    <div className="space-y-4">
                        {products.map((product) => (
                            <ProductRow key={product.id} product={product} />
                        ))}
                    </div>

                    {pageData && (
                        <div className="mt-6 flex items-center justify-between">
                            <div className="text-sm text-slate-500">
                                Страница {pageData.number + 1} из {pageData.totalPages || 1}, всего товаров: {pageData.totalElements}
                            </div>

                            <div className="flex gap-2">
                                <button
                                    onClick={goToPrevPage}
                                    disabled={page === 0}
                                    className="rounded-2xl border px-4 py-2 text-sm disabled:opacity-50"
                                >
                                    Prev
                                </button>

                                <button
                                    onClick={goToNextPage}
                                    disabled={!pageData || pageData.last}
                                    className="rounded-2xl border px-4 py-2 text-sm disabled:opacity-50"
                                >
                                    Next
                                </button>
                            </div>
                        </div>
                    )}
                </>
            )}
        </div>
    );
}