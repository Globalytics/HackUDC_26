package com.decisiontool.config;

import com.decisiontool.dto.UploadDatasetRequest;
import com.decisiontool.service.DataService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class DatasetInitializer implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(DatasetInitializer.class);

    private final DataService dataService;

    public DatasetInitializer(DataService dataService) {
        this.dataService = dataService;
    }

    @Override
    public void run(ApplicationArguments args) {
        log.info("Cargando datasets de ejemplo...");
        loadSampleDataset();
        log.info("Datasets de ejemplo cargados. Total: {}", dataService.listAll().size());
    }

    private void loadSampleDataset() {
        String csv = """
                producto,ventas,margen,region
                Producto A,15000,0.32,Norte
                Producto B,22000,0.28,Sur
                Producto C,8500,0.45,Este
                Producto D,31000,0.21,Oeste
                Producto E,19000,0.38,Norte
                """;

        dataService.upload(UploadDatasetRequest.builder()
                .name("Ventas por Producto")
                .description("Dataset de ejemplo con ventas, margen y región por producto")
                .content(csv)
                .format("csv")
                .build());
    }
}