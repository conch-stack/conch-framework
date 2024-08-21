package com.nabob.conch.sample.bootenhance.importselect;

/**
 * CacheConfigurationSelector
 *
 * @author Adam
 * @since 2024/8/12
 */
public class CacheConfigurationSelector extends CacheModeImportSelector<EnableImportSelectTest> {

    @Override
    protected String[] selectImports(CacheMode cacheMode) {
        switch (cacheMode) {
            case DAO_SERVICE:
                return new String[]{CacheServiceConfiguration.class.getName()};
            case DAO_CLIENT:
                return new String[]{CacheClientConfiguration.class.getName()};
            default:
                return null;
        }
    }
}
