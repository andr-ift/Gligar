package com.opensooq.supernova.gligar.utils

import android.net.Uri
import android.provider.MediaStore

/**
 * Created by Hani AlMomani on 24,September,2019
 */

internal val cursorUri: Uri = MediaStore.Files.getContentUri("external")
internal const val ORDER_BY = MediaStore.Images.Media.DATE_TAKEN + " DESC"
internal const val ID_COLUMN = MediaStore.Files.FileColumns._ID
internal const val PATH_COLUMN = MediaStore.Files.FileColumns.DATA
internal const val MIME_TYPE = MediaStore.MediaColumns.MIME_TYPE
internal const val PAGE_SIZE = 30

internal const val IMAGES = "images"
internal const val ALBUMS = "albums"
internal const val PHOTO_PATH = "photo_path"
internal const val ALBUM_POS = "album_pos"
internal const val PAGE = "page"
internal const val SELECTED_ALBUM = "selected_album"
internal const val SELECTED_IMAGES = "selected_images"
internal const val CURRENT_SELECTION = "curren_selection"
internal const val LIMIT = "limit"
internal const val DISABLE_CAMERA = "limit"
