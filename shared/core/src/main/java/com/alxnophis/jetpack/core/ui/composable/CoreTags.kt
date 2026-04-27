package com.alxnophis.jetpack.core.ui.composable

/**
 * Centralized test tags for UI testing.
 * These tags should be used in production code with Modifier.testTag()
 * and in tests with onNodeWithTag() / onAllNodesWithTag().
 *
 * Benefits:
 * - Decouples tests from accessibility strings (contentDescription)
 * - Allows accessibility labels to remain user-meaningful and localized
 * - Provides stable test identifiers independent of UI text changes
 */
object CoreTags {
    // Core navigation
    const val TAG_CORE_BACK = "core_back"

    // Posts feature
    const val TAG_POSTS_LIST = "posts_list"
    const val TAG_POST_ITEM = "post_item"
    const val TAG_POST_DETAIL = "post_detail"
}
