#include <stdarg.h>
#include <stddef.h>
#include <setjmp.h>
#include <cmocka.h>

#include "../src/ly_helper.h"

static void test_context_creation(void **state) {
  struct ly_ctx *ctx = create_context("../yang");
  assert_non_null(ctx);
  ly_ctx_destroy(ctx);
}

static void test_module_loading(void **state) {
  struct ly_ctx *ctx = create_context("../yang");
  assert_non_null(ctx);

  assert_int_equal(load_module(ctx, "example"), 0);

  ly_ctx_destroy(ctx);
}

static void test_valid_data(void **state) {
  struct ly_ctx *ctx = create_context("../yang");
  assert_non_null(ctx);
  load_module(ctx, "example");

  const char *xml =
      "<system xmlns=\"urn:example\">"
      "<hostname>router1</hostname>"
      "</system>";

  assert_int_equal(validate_data(ctx, xml), 0);
  ly_ctx_destroy(ctx);
}

static void test_invalid_data(void **state) {
  struct ly_ctx *ctx = create_context("../yang");
  assert_non_null(ctx);
  load_module(ctx, "example");

  const char *xml =
      "<system xmlns=\"urn:example\">"
      "<unknown>bad</unknown>"
      "</system>";

  assert_int_not_equal(validate_data(ctx, xml), 0);
  ly_ctx_destroy(ctx);
}

int main(void) {
  const struct CMUnitTest tests[] = {
    cmocka_unit_test(test_context_creation),
    cmocka_unit_test(test_module_loading),
    cmocka_unit_test(test_valid_data),
    cmocka_unit_test(test_invalid_data),
};

  return cmocka_run_group_tests(tests, NULL, NULL);
}