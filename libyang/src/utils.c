
#include "utils.h"
#include <stdio.h>
#include <stdlib.h>

char *read_file(const char *path) {
    FILE *f = fopen(path, "rb");
    fseek(f, 0, SEEK_END);
    long size = ftell(f);
    rewind(f);
    char *buffer = malloc(size + 1);
    fread(buffer, 1, size, f);
    buffer[size] = 0;
    fclose(f);
    return buffer;
}

struct lyd_node *parse_json(struct ly_ctx *ctx, const char *file, int flags, int valid_flags) {
    char *json = read_file(file);
    struct lyd_node *tree = NULL;

    if (lyd_parse_data_mem(ctx, json,
                           LYD_JSON,
                           flags,
                           LYD_VALIDATE_PRESENT,
                           &tree) != LY_SUCCESS) {
        free(json);
        return NULL;
    }

    free(json);
    return tree;
}
