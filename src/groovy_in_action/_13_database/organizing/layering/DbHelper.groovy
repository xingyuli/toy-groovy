package groovy_in_action._13_database.organizing.layering

@Grab('org.hsqldb:hsqldb:2.4.0')
@GrabConfig(systemClassLoader = true)
import groovy.sql.Sql
import groovy.text.SimpleTemplateEngine as STE

import org.hsqldb.jdbc.JDBCDataSource

class DbHelper {
    Sql db

    DbHelper() {
        db = new Sql(new JDBCDataSource(
                database: 'jdbc:hsqldb:mem:GinA', user: 'sa', password: ''))
    }

    def simpleTemplate = new STE().createTemplate('''
    DROP   TABLE $name IF EXISTS;
    CREATE TABLE $name (
      ${lowname}Id INTEGER GENERATED BY DEFAULT AS IDENTITY,
    $fields
    );
    ''')

    def executeDdl(DataAccessObject dao) {
        def template = simpleTemplate
        def binding = [
            name: dao.tablename,
            lowname: dao.tablename.toLowerCase(),
            fields: dao.schema.collect { key, val ->
              "    ${key.padRight(12)} $val" }.join(",\n")
        ]
        def stmt = template.make(binding).toString()
        db.execute stmt
    }

}