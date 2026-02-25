#include "../src/utils.h"
#include <setjmp.h>
#include <cmocka.h>
#include <libyang/libyang.h>
#include <stddef.h>

static struct ly_ctx* ctx;

static int setup(void** state) {
  /* Create empty context */
  if (ly_ctx_new(NULL, 0, &ctx) != LY_SUCCESS) {
    return -1;
  }

  if (ly_ctx_set_searchdir(ctx, "../yang-context") != LY_SUCCESS) {
    return -1;
  }

  /* Add local YANG directories */
  if (ly_ctx_set_searchdir(ctx, "../yang") != LY_SUCCESS) {
    return -1;
  }

  ly_log_level(LY_LLDBG);

  return 0;
}

static int teardown(void** state) {
  ly_ctx_destroy(ctx);
  return 0;
}


/* Load modules */
static void test_load_modules(void** state) {
  assert_non_null(ly_ctx_load_module(ctx, "schema-test", NULL, NULL));
  assert_non_null(ly_ctx_load_module(ctx, "nmda-test", NULL, NULL));
}

/* Schema validation */
static void test_schema_validation(void** state) {
  assert_non_null(ly_ctx_load_module(ctx, "schema-test", NULL, NULL));
}

/* Data validation */
// TODO figure out how to free correctly
static void test_data_validation(void** state) {
  ly_ctx_load_module(ctx, "schema-test", NULL, NULL);
  struct lyd_node* tree = parse_json(ctx, "../data/valid.json", LYD_PARSE_STRICT,LYD_VALIDATE_PRESENT);
  assert_non_null(tree);
  lyd_free_all(tree);

  struct lyd_node* tree_invalid = parse_json(ctx, "../data/invalid.json", LYD_PARSE_STRICT,LYD_VALIDATE_PRESENT);
  assert_null(tree_invalid);

  struct lyd_node* tree_parse_only = parse_json(ctx, "../data/invalid.json", LYD_PARSE_ONLY,LYD_VALIDATE_PRESENT);
  assert_non_null(tree_parse_only);
  lyd_free_all(tree_parse_only);
}

/* Module inspection */
/* Can be done without loading the module into the global context by
 * making a new temporary context
 */
static void test_module_inspection(void** state) {
  const struct lys_module* mod =
    ly_ctx_load_module(ctx, "schema-test", NULL, NULL);
  assert_string_equal(mod->name, "schema-test");
}

/* JSON encoding */
static void test_json_encoding(void** state) {
  ly_ctx_load_module(ctx, "schema-test", NULL, NULL);
  struct lyd_node* tree = parse_json(ctx, "../data/valid.json", 0,LYD_VALIDATE_PRESENT);
  char* out = NULL;
  lyd_print_mem(&out, tree, LYD_JSON, LYD_PRINT_SIBLINGS);
  assert_non_null(out);
  printf("%s", out);
  free(out);
  lyd_free_all(tree);
}

/* XPath extraction TODO add for structure */
static void test_xpath(void** state) {
  ly_ctx_load_module(ctx, "schema-test", NULL, NULL);

  struct lyd_node* tree = parse_json(ctx, "../data/xpath.json", LYD_PARSE_STRICT,LYD_VALIDATE_PRESENT);
  assert_non_null(tree);

  struct ly_set* set = NULL;

  LY_ERR ret = lyd_find_xpath(tree, "/schema-test:system/hostname", &set);

  assert_int_equal(ret, LY_SUCCESS);
  assert_non_null(set);
  assert_int_equal(set->count, 1);

  ly_set_free(set, NULL);
  lyd_free_all(tree);
}

/* YANG library - full validation */
static void test_yang_library(void** state) {
  const struct lys_module* yl =
    ly_ctx_load_module(ctx, "ietf-yang-library", "2019-01-04", NULL);
  assert_non_null(yl);

  assert_non_null(
    ly_ctx_load_module(ctx, "ietf-datastores", "2018-02-14", NULL));
  assert_non_null(ly_ctx_load_module(ctx, "nmda-test", NULL, NULL));

  struct lyd_node* tree = NULL;

  assert_int_equal(lyd_parse_data_path(ctx, "../data/yang-library.json",
                     LYD_JSON, LYD_PARSE_STRICT, 0, &tree),
                   LY_SUCCESS);
  assert_non_null(tree);

  assert_int_equal(lyd_validate_all(&tree, ctx, 0, NULL), LY_SUCCESS);
  lyd_free_all(tree);
}

/* YANG Push TODO parse actual module and craft + validate some values for it */
static void test_yang_push(void** state) {
  assert_null(ly_ctx_get_module(ctx, "ietf-yang-push", NULL));
}

// FIXME
static void test_nmda(void** state) {
  return;

  /* Load required schema modules */
  assert_non_null(
    ly_ctx_load_module(ctx, "ietf-yang-library", "2019-01-04", NULL));
  assert_non_null(
    ly_ctx_load_module(ctx, "ietf-datastores", "2018-02-14", NULL));
  assert_non_null(ly_ctx_load_module(ctx, "nmda-test", NULL, NULL));

  struct lyd_node* yl = NULL;
  struct lyd_node* running = NULL;
  struct lyd_node* running_invalid = NULL;
  struct lyd_node* intended = NULL;
  struct lyd_node* oper = NULL;

  /* ---- Load YANG Library data FIRST ---- */
  assert_int_equal(lyd_parse_data_path(ctx, "../data/yang-library.json",
                     LYD_JSON, LYD_PARSE_STRICT, 0, &yl),
                   LY_SUCCESS);

  assert_int_equal(lyd_validate_all(&yl, ctx, 0, NULL), LY_SUCCESS);

  /* ---- Running datastore (valid config only) ---- */
  assert_int_equal(lyd_parse_data_path(ctx, "../data/nmda-running.json",
                     LYD_JSON, LYD_PARSE_STRICT, 0, &running),
                   LY_SUCCESS);

  assert_int_equal(lyd_validate_all(&running, ctx, LYD_VALIDATE_NO_STATE, NULL),
                   LY_SUCCESS);

  /* ---- Running datastore (invalid: contains state data) ---- */
  assert_int_equal(lyd_parse_data_path(ctx, "../data/nmda-running-invalid.json",
                     LYD_JSON, LYD_PARSE_STRICT, 0,
                     &running_invalid),
                   LY_SUCCESS);

  assert_int_not_equal(
    lyd_validate_all(&running_invalid, ctx, LYD_VALIDATE_NO_STATE, NULL),
    LY_SUCCESS);

  /* ---- Intended datastore ---- */
  assert_int_equal(lyd_parse_data_path(ctx, "../data/nmda-intended.json",
                     LYD_JSON, LYD_PARSE_STRICT, 0,
                     &intended),
                   LY_SUCCESS);

  assert_int_equal(
    lyd_validate_all(&intended, ctx, LYD_VALIDATE_NO_STATE, NULL),
    LY_SUCCESS);

  /* ---- Operational datastore ---- */
  assert_int_equal(lyd_parse_data_path(ctx, "../data/nmda-operational.json",
                     LYD_JSON, LYD_PARSE_STRICT, 0, &oper),
                   LY_SUCCESS);

  assert_int_equal(lyd_validate_all(&oper, ctx, 0, NULL), LY_SUCCESS);

  /* ---- Simulate operational view ---- */
  assert_int_equal(lyd_merge_tree(&running, oper, LYD_MERGE_DEFAULTS),
                   LY_SUCCESS);

  assert_int_equal(lyd_validate_all(&running, ctx, 0, NULL), LY_SUCCESS);

  /* Cleanup */
  lyd_free_all(yl);
  lyd_free_all(running);
  lyd_free_all(running_invalid);
  lyd_free_all(intended);
  lyd_free_all(oper);
}

static void test_structure_validation(void** state) {
  struct lyd_node* tree = NULL;
  const struct lys_module* mod;

  /* Load required modules */
  assert_non_null(ly_ctx_load_module(ctx, "struct-test", NULL, NULL));

  /* Get module */
  mod = ly_ctx_get_module_implemented(ctx, "struct-test");
  assert_non_null(mod);

  struct lysc_ext_instance* ext = NULL;
  if (mod->compiled->exts) {
    LY_ARRAY_FOR(mod->compiled->exts, struct lysc_ext_instance, ext) {
      printf("Extension instance: %s\n", ext->def->name);

      if (ext->argument) {
        printf("  argument: %s\n", ext->argument);
      }

      /* For yang-data, ext->substmts contain the structure tree */
      if (ext->substmts) {
        printf("  has substatements (structure body)\n");
      }
    }
  }
  else {
    printf("no exts\n");
  }

  /* Parse structure data */
  assert_int_equal(
    lyd_parse_data_path(ctx,
      "../data/structure.json",
      LYD_JSON,
      LYD_PARSE_STRICT,
      0,
      &tree),
    LY_SUCCESS);


  assert_non_null(mod);

  /* Validate structure */
  assert_int_equal(
    lyd_validate_op(tree,
      NULL,
      LYD_TYPE_DATA_YANG,
      NULL),
    LY_SUCCESS);

  lyd_free_all(tree);
}

static void test_structure_invalid(void** state) {
  struct lyd_node* tree = NULL;

  /* Load required modules */
  assert_non_null(ly_ctx_load_module(ctx, "struct-test", NULL, NULL));

  assert_int_equal(
    lyd_parse_data_path(ctx,
      "../data/structure-invalid.json",
      LYD_JSON,
      LYD_PARSE_STRICT | LYD_PARSE_ONLY,
      0,
      &tree),
    LY_SUCCESS);

  assert_int_not_equal(
    lyd_validate_op(tree,
      NULL,
      LYD_TYPE_DATA_YANG,
      NULL),
    LY_SUCCESS);

  lyd_free_all(tree);
}

/* anydata validation */
static void test_anydata_validation(void** state) {
  ly_ctx_load_module(ctx, "anydata-test", NULL, NULL);
  ly_ctx_load_module(ctx, "example", NULL, NULL);
  struct lyd_node* tree = parse_json(ctx, "../data/anydata.json", LYD_PARSE_STRICT,LYD_VALIDATE_PRESENT);
  assert_non_null(tree);
  lyd_free_all(tree);
}

static void test_xpath_behind_anydata(void** state) {
  (void)state;

  struct lyd_node* tree = NULL;
  struct ly_set* set = NULL;
  /* Load modules */
  assert_non_null(ly_ctx_load_module(ctx, "anydata-test", NULL, NULL));
  assert_non_null(ly_ctx_load_module(ctx, "example", NULL, NULL));

  /* Parse normally */
  tree = parse_json(ctx, "../data/anydata.json", LYD_PARSE_STRICT,LYD_VALIDATE_PRESENT);
  assert_non_null(tree);

  /* XPath trying to go behind anydata */
  assert_int_equal(
    lyd_find_xpath(tree,
      "/anydata-test:root/payload/example:system/hostname",
      &set),
    LY_SUCCESS);

  /* Empty set expected */
  assert_non_null(set);

  const char* value = lyd_get_value(set->dnodes[0]);

  assert_string_equal(value, "routerX");

  assert_int_equal(set->count, 1);

  ly_set_free(set, NULL);
  lyd_free_all(tree);
}

/* Schema comparison */
static void test_schema_comparison(void** state) {
  const struct lys_module* m1 =
    ly_ctx_load_module(ctx, "schema-test", NULL, NULL);
  const struct lys_module* m2 = ly_ctx_get_module(ctx, "schema-test", NULL);

  assert_string_equal(m1->name, m2->name);

  if (m1->revision == NULL ^ m2->revision == NULL) {
    assert_string_equal(m1->revision, m2->revision);
  }
}

/* Telemetry unsupported */
static void test_telemetry(void** state) {
  assert_null(ly_ctx_get_module(ctx, "broker-telemetry-msg", NULL));
}

/* Envelope unsupported */
static void test_envelope(void** state) {
  assert_null(ly_ctx_get_module(ctx, "notif-envelope", NULL));
}

/* CBOR (compile-time check) TODO when implemented */
static void test_cbor(void** state) {
#ifdef LYD_CBOR
  assert_true(1);
#else
  assert_true(1);
#endif
}

/* YANG to YANG transform */
static void test_yang_to_yang(void** state) {
  const struct lys_module* mod =
    ly_ctx_load_module(ctx, "schema-test", NULL, NULL);
  char* out = NULL;
  lys_print_mem(&out, mod, LYS_OUT_YANG, 0);
  assert_non_null(out);
  free(out);
}

/* Structures */
static void test_structures(void** state) {
  assert_non_null(ly_ctx_load_module(ctx, "struct-test", NULL, NULL));
}

int main(void) {
  const struct CMUnitTest tests[] = {
    cmocka_unit_test_setup_teardown(test_module_inspection, setup, teardown),
    cmocka_unit_test_setup_teardown(test_load_modules, setup, teardown),
    cmocka_unit_test_setup_teardown(test_schema_validation, setup, teardown),
    cmocka_unit_test_setup_teardown(test_json_encoding, setup, teardown),
    cmocka_unit_test_setup_teardown(test_data_validation, setup, teardown),
    cmocka_unit_test_setup_teardown(test_anydata_validation, setup, teardown),
    cmocka_unit_test_setup_teardown(test_xpath, setup, teardown),
    cmocka_unit_test_setup_teardown(test_xpath_behind_anydata, setup, teardown),
    cmocka_unit_test_setup_teardown(test_structures, setup, teardown),
    cmocka_unit_test_setup_teardown(test_yang_library, setup, teardown),
    // cmocka_unit_test_setup_teardown(test_yang_push, setup, teardown),
    // cmocka_unit_test_setup_teardown(test_nmda, setup, teardown),
    // cmocka_unit_test_setup_teardown(test_schema_comparison, setup, teardown),
    // cmocka_unit_test_setup_teardown(test_telemetry, setup, teardown),
    // cmocka_unit_test_setup_teardown(test_envelope, setup, teardown),
    // cmocka_unit_test_setup_teardown(test_cbor, setup, teardown),
    // cmocka_unit_test_setup_teardown(test_yang_to_yang, setup, teardown),
    cmocka_unit_test_setup_teardown(test_structure_validation, setup, teardown),
    cmocka_unit_test_setup_teardown(test_structure_invalid, setup, teardown),
  };
  return cmocka_run_group_tests(tests, NULL, NULL);
}
