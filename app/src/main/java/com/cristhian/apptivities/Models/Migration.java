package com.cristhian.apptivities.Models;

import io.realm.DynamicRealm;
import io.realm.DynamicRealmObject;
import io.realm.FieldAttribute;
import io.realm.RealmMigration;
import io.realm.RealmObjectSchema;
import io.realm.RealmSchema;

/**
 * Example of migrating a Realm file from version 0 (initial version) to its last version (version 3).
 */
public class Migration implements RealmMigration {

    @Override
    public void migrate(final DynamicRealm realm, long oldVersion, long newVersion) {
        // During a migration, a DynamicRealm is exposed. A DynamicRealm is an untyped variant of a normal Realm, but
        // with the same object creation and query capabilities.
        // A DynamicRealm uses Strings instead of Class references because the Classes might not even exist or have been
        // renamed.

        // Access the Realm schema in order to create, modify or delete classes and their fields.
        RealmSchema schema = realm.getSchema();

        /************************************************
         // Version 0
         class Actividad
         long categoria;
         // Version 1
         class Actividad
         @Index                 // Añadir indexado al campo categoria
         long categoria;
         ************************************************/
        // Migrate from version 0 to version 1
        if (oldVersion == 1) {
            RealmObjectSchema actividadSchema = schema.get("Actividad");
            actividadSchema
                    .addField("categoria1", long.class, FieldAttribute.INDEXED)
                    .transform(new RealmObjectSchema.Function() {
                        @Override
                        public void apply(DynamicRealmObject obj) {
                            obj.set("categoria1", obj.getLong("categoria"));
                        }
                    })
                    .removeField("categoria")
                    .renameField("categoria1", "categoria");

            oldVersion++;
        }
    }
}
