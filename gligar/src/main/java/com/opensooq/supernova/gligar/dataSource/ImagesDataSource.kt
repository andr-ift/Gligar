package com.opensooq.supernova.gligar.dataSource

import android.content.ContentResolver
import android.database.Cursor
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import com.opensooq.OpenSooq.ui.imagePicker.model.AlbumItem
import com.opensooq.OpenSooq.ui.imagePicker.model.MediaItem
import com.opensooq.OpenSooq.ui.imagePicker.model.ImageSource
import com.opensooq.supernova.gligar.utils.*

/**
 * Created by Hani AlMomani on 24,September,2019
 */

internal class ImagesDataSource(private val contentResolver: ContentResolver){

    private val selection = (MediaStore.Files.FileColumns.MEDIA_TYPE + "="
            + MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE
            + " OR "
            + MediaStore.Files.FileColumns.MEDIA_TYPE + "="
            + MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO)

    fun loadAlbums(): ArrayList<AlbumItem> {

        val albumCursor = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            contentResolver.query(
                cursorUri,
                arrayOf(
                    MediaStore.Files.FileColumns.BUCKET_DISPLAY_NAME,
                    MediaStore.Files.FileColumns.BUCKET_ID
                ),
                selection,
                null,
                ORDER_BY
            )
        } else {
            contentResolver.query(
                cursorUri,
                arrayOf(
                    "bucket_display_name",
                    "bucket_id"
                ),
                selection,
                null,
                ORDER_BY
            )
        }

        val list = arrayListOf<AlbumItem>()
        try {
            list.add(AlbumItem("All", true, "0"))
            if (albumCursor == null) {
                return list
            }
            albumCursor.doWhile {
                val bucketId = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                    albumCursor.getString(albumCursor.getColumnIndex(MediaStore.Files.FileColumns.BUCKET_ID))
                } else {
                    albumCursor.getString(albumCursor.getColumnIndex("bucket_id"))
                }
                val name = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                    albumCursor.getString(albumCursor.getColumnIndex(MediaStore.Files.FileColumns.BUCKET_DISPLAY_NAME)) ?: bucketId
                } else {
                    albumCursor.getString(albumCursor.getColumnIndex("bucket_display_name")) ?: bucketId
                }
                val albumItem = AlbumItem(name, false, bucketId)
                if (!list.contains(albumItem) && !name.isNullOrEmpty() && !bucketId.isNullOrEmpty()) {
                    list.add(albumItem)
                }
            }
        } finally {
            if (albumCursor != null && !albumCursor.isClosed) {
                albumCursor.close()
            }
        }
        return list
    }

    fun loadAlbumImages(
        albumItem: AlbumItem?,
        page: Int
    ): ArrayList<MediaItem> {
        val offset = page * PAGE_SIZE
        val list: ArrayList<MediaItem> = arrayListOf()
        var photoCursor: Cursor? = null
        try {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                val bundle = Bundle().apply {
                    putInt(ContentResolver.QUERY_ARG_LIMIT, PAGE_SIZE)
                    putInt(ContentResolver.QUERY_ARG_OFFSET, offset)
                    putString(ContentResolver.QUERY_ARG_SQL_SORT_ORDER, ORDER_BY)
                    if (albumItem != null && !albumItem.isAll) {
                        putString(
                            ContentResolver.QUERY_ARG_SQL_SELECTION,
                            "${MediaStore.Files.FileColumns.BUCKET_ID} =?"
                        )
                        putStringArray(ContentResolver.QUERY_ARG_SQL_SELECTION_ARGS, arrayOf(albumItem.bucketId))
                    } else putString(ContentResolver.QUERY_ARG_SQL_SELECTION, selection)
                }
                photoCursor = contentResolver.query(
                    cursorUri,
                    arrayOf(
                        ID_COLUMN,
                        PATH_COLUMN,
                        MIME_TYPE
                    ),
                    bundle,
                    null
                )
            } else {
                photoCursor = contentResolver.query(
                    cursorUri,
                    arrayOf(
                        ID_COLUMN,
                        PATH_COLUMN,
                        MIME_TYPE
                    ),
                    if (albumItem == null || albumItem.isAll) selection else "${MediaStore.Files.FileColumns.BUCKET_ID} =?",
                    if (albumItem == null || albumItem.isAll) null else arrayOf(albumItem.bucketId),
                    "$ORDER_BY LIMIT $PAGE_SIZE OFFSET $offset"
                )
            }
            photoCursor?.isAfterLast ?: return list
            photoCursor.doWhile {
                val image = photoCursor.getString((photoCursor.getColumnIndex(PATH_COLUMN)))
                val mimeType = photoCursor.getString((photoCursor.getColumnIndex(MIME_TYPE))) ?: "null"
                list.add(MediaItem(mimeType.contains("video"), image, ImageSource.GALLERY, 0))
            }
        } finally {
            if (photoCursor != null && !photoCursor.isClosed) {
                photoCursor.close()
            }
        }
        return list
    }
}