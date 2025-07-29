import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import praktikum.Bun;
import praktikum.Burger;
import praktikum.Ingredient;
import praktikum.IngredientType;

import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

/**
 * Тестовый класс для проверки функциональности класса Burger.
 * Использует Mockito для создания заглушек и Parameterized для параметризованных тестов.
 */
@RunWith(Parameterized.class) // Аннотация для запуска теста с параметрами
public class BurgerTest {

    private Burger burger; // Экземпляр тестируемого класса

    // Создаем mock-объекты с помощью Mockito
    @Mock
    private Bun bun;
    @Mock
    private Ingredient ingredient1;
    @Mock
    private Ingredient ingredient2;
    @Mock
    private Ingredient ingredient3;

    // Параметры для параметризованного теста
    @Parameterized.Parameter
    public float bunPrice; // Цена булочки
    @Parameterized.Parameter(1)
    public float ingredient1Price; // Цена первого ингредиента
    @Parameterized.Parameter(2)
    public float ingredient2Price; // Цена второго ингредиента
    @Parameterized.Parameter(3)
    public float expectedPrice; // Ожидаемая общая цена

    /**
     * Метод предоставляет данные для параметризованного теста.
     * Каждый массив содержит: цену булочки, цены двух ингредиентов и ожидаемую общую цену.
     */
    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
                {100f, 50f, 75f, 325f},  // bun price * 2 + ingredients
                {200f, 100f, 150f, 650f},
                {50f, 25f, 25f, 150f}
        });
    }

    /**
     * Метод инициализации перед каждым тестом.
     * Настраивает mock-объекты и создает новый экземпляр Burger.
     */
    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this); // Инициализация mock-объектов
        burger = new Burger(); // Создаем новый бургер перед каждым тестом

        // Настраиваем поведение mock-булочки
        when(bun.getPrice()).thenReturn(bunPrice);
        when(bun.getName()).thenReturn("test bun");

        // Настраиваем поведение mock-ингредиентов
        when(ingredient1.getPrice()).thenReturn(ingredient1Price);
        when(ingredient1.getName()).thenReturn("ingredient1");
        when(ingredient1.getType()).thenReturn(IngredientType.SAUCE);

        when(ingredient2.getPrice()).thenReturn(ingredient2Price);
        when(ingredient2.getName()).thenReturn("ingredient2");
        when(ingredient2.getType()).thenReturn(IngredientType.FILLING);

        when(ingredient3.getPrice()).thenReturn(ingredient2Price);
        when(ingredient3.getName()).thenReturn("ingredient3");
        when(ingredient3.getType()).thenReturn(IngredientType.SAUCE);
    }

    /**
     * Тест проверяет корректность установки булочки в бургер.
     */
    @Test
    public void testSetBuns() {
        burger.setBuns(bun); // Устанавливаем булочку
        assertEquals("Булочка должна быть установлена", bun, burger.bun);
    }

    /**
     * Тест проверяет добавление ингредиента в бургер.
     */
    @Test
    public void testAddIngredient() {
        burger.addIngredient(ingredient1); // Добавляем ингредиент
        assertEquals("Должен быть добавлен один ингредиент", 1, burger.ingredients.size());
        assertEquals("Добавленный ингредиент должен совпадать", ingredient1, burger.ingredients.get(0));
    }

    /**
     * Тест проверяет удаление ингредиента из бургера.
     */
    @Test
    public void testRemoveIngredient() {
        // Добавляем два ингредиента
        burger.addIngredient(ingredient1);
        burger.addIngredient(ingredient2);

        burger.removeIngredient(0); // Удаляем первый ингредиент

        assertEquals("Должен остаться один ингредиент", 1, burger.ingredients.size());
        assertEquals("Оставшийся ингредиент должен быть ingredient2", ingredient2, burger.ingredients.get(0));
    }

    /**
     * Тест проверяет перемещение ингредиента в бургере.
     */
    @Test
    public void testMoveIngredient() {
        // Добавляем три ингредиента
        burger.addIngredient(ingredient1);
        burger.addIngredient(ingredient2);
        burger.addIngredient(ingredient3);

        // Перемещаем первый ингредиент на позицию 2
        burger.moveIngredient(0, 2);

        // Проверяем новый порядок ингредиентов
        assertEquals("Первый элемент должен быть ingredient2", ingredient2, burger.ingredients.get(0));
        assertEquals("Второй элемент должен быть ingredient3", ingredient3, burger.ingredients.get(1));
        assertEquals("Третий элемент должен быть ingredient1", ingredient1, burger.ingredients.get(2));
    }

    /**
     * Тест проверяет расчет стоимости Бургера.
     */
    @Test
    public void testGetPrice() {
        burger.setBuns(bun); // Устанавливаем булочку
        burger.addIngredient(ingredient1); // Добавляем ингредиенты
        burger.addIngredient(ingredient2);

        // Ожидаемая цена: цена булочки * 2 + цены ингредиентов
        float expected = bunPrice * 2 + ingredient1Price + ingredient2Price;
        assertEquals("Цена должна быть рассчитана правильно", expected, burger.getPrice(), 0.01);
    }

    /**
     * Тест проверяет формирование чека бургера.
     */
    @Test
    public void testGetReceipt() {
        burger.setBuns(bun);
        burger.addIngredient(ingredient1);
        burger.addIngredient(ingredient2);

        String receipt = burger.getReceipt();

        // Проверяем основные части чека
        assertTrue("Чек должен содержать название булочки",
                receipt.contains(String.format("(==== %s ====)", bun.getName())));

        assertTrue("Чек должен содержать первый ингредиент",
                receipt.contains(String.format("= %s %s =",
                        ingredient1.getType().toString().toLowerCase(),
                        ingredient1.getName())));

        assertTrue("Чек должен содержать второй ингредиент",
                receipt.contains(String.format("= %s %s =",
                        ingredient2.getType().toString().toLowerCase(),
                        ingredient2.getName())));

        // Проверяем цену с учетом форматирования
        float totalPrice = bun.getPrice() * 2 + ingredient1.getPrice() + ingredient2.getPrice();
        String expectedPriceLine = String.format("Price: %.2f", totalPrice);
        assertTrue("Чек должен содержать строку с ценой: " + expectedPriceLine,
                receipt.contains(expectedPriceLine));
    }

    /**
     * Параметризованный тест проверяет расчет стоимости с разными входными данными.
     */
    @Test
    public void testParameterizedGetPrice() {
        burger.setBuns(bun);
        burger.addIngredient(ingredient1);
        burger.addIngredient(ingredient2);
        assertEquals("Цена должна соответствовать ожидаемой для параметров: "
                        + bunPrice + ", " + ingredient1Price + ", " + ingredient2Price,
                expectedPrice, burger.getPrice(), 0.01);
    }
}