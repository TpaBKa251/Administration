ALTER TABLE "administration"."document"
    DROP CONSTRAINT document_user_key,
    ADD CONSTRAINT uq_documents_user_and_type UNIQUE ("user", "type");